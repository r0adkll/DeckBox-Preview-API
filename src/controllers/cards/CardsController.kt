package com.deckboxtcg.endpoints

import com.deckboxtcg.controllers.cards.locations.CardById
import com.deckboxtcg.controllers.cards.locations.Cards
import com.deckboxtcg.data.DataSource
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Routing

fun Routing.cards(dataSource: DataSource) {

    get<Cards> { filter ->
        val cards = dataSource.cards.filter(filter.filter)
        call.respond(mapOf("cards" to cards))
    }

    get<CardById> { (id) ->
        val card = dataSource.cards.find { it.id == id }
        if (card != null) {
            call.respond(mapOf("card" to card))
        } else {
            call.respond(HttpStatusCode.NotFound, "No card found for $id")
        }
    }
}