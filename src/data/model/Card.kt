package data.model

data class Card(
        val id: String,
        val name: String,
        val nationalPokedexNumber: Int?,
        val imageUrl: String,
        val imageUrlHiRes: String,
        val types: List<String>?,
        val supertype: String,
        val subtype: String,
        val evolvesFrom: String?,
        val hp: String?,
        val retreatCost: List<String>?,
        val number: String,
        val artist: String?,
        val rarity: String?,
        val series: String,
        val set: String,
        val setCode: String,
        val text: MutableList<String>?,
        val attacks: MutableList<Attack>?,
        val weaknesses: List<Effect>?,
        val resistances: List<Effect>?,
        val ability: Ability?
)