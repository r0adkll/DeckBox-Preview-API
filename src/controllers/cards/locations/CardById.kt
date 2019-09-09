package com.deckboxtcg.controllers.cards.locations

import io.ktor.locations.Location

@Location("/api/v1/cards/{id}")
data class CardById(val id: String)