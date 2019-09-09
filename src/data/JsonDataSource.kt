package com.deckboxtcg.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.model.Card
import data.model.Expansion
import okio.buffer
import okio.source

class JsonDataSource(fileName: String) : DataSource {

    private val cardData: List<Card>

    val expansion = Expansion(
        "sm12",
        "COE",
        "Cosmic Eclipse",
        "Sun & Moon",
        208,
        false,
        false,
        "November 1, 2019",
        "https://deckboxtcg.app/images/sm12/symbol.png",
        "https://deckboxtcg.app/images/sm12/logo.png"
    )

    init {
        val json = JsonDataSource::class.java.getResourceAsStream(fileName)
            .source()
            .buffer()
            .readString(Charsets.UTF_8)

        val cardsType = object : TypeToken<List<Card>>() {}.type
        cardData = Gson().fromJson(json, cardsType)
    }

    override val sets: List<Expansion>
        get() = listOf(expansion)

    override val cards: List<Card>
        get() = cardData
}