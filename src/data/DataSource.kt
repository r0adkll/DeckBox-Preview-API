package com.deckboxtcg.data

import data.model.Card
import data.model.Expansion

interface DataSource {

    val sets: List<Expansion>
    val cards: List<Card>
}