package com.deckboxtcg.controllers.sets

import com.deckboxtcg.data.DataSource
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.sets(dataSource: DataSource) {

    get("/api/v1/sets") {
        call.respond(mapOf("sets" to dataSource.sets))
    }

    get("/api/v1/sets/{id}") {
        val setId = call.parameters["id"]
        dataSource.sets.find { it.code == setId }?.let {
            call.respond(mapOf("set" to it))
        } ?: call.respond(HttpStatusCode.NotFound, "No set found for $setId")
    }
}