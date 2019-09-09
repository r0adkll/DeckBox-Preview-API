package com.deckboxtcg.controllers.cards.locations

import io.ktor.locations.Location

@Location("/cards/{id}")
data class CardById(val id: String)