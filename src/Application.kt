package com.deckboxtcg

import com.deckboxtcg.controllers.sets.sets
import com.deckboxtcg.data.DataSource
import com.deckboxtcg.controllers.cards.cards
import com.deckboxtcg.internal.di.appModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.request.path
import io.ktor.routing.routing
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        gson {
            disableHtmlEscaping()
        }
    }

    install(Locations)

    install(Koin) {
        modules(appModule)
    }

    val dataSource by inject<DataSource>()

    routing {

        cards(dataSource)
        sets(dataSource)
    }
}

