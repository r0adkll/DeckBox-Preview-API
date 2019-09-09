package com.deckboxtcg.controllers.cards.locations

import com.deckboxtcg.controllers.cards.locations.Cards.ModifierFilter.Modifier.*
import data.model.Card
import io.ktor.locations.Location

@Location("/cards")
data class Cards(
    val name: String?,
    val id: String?,
    val nationalPokedexNumber: Int?,
    val types: String?,
    val subtype: String?,
    val supertype: String?,
    val hp: String?,
    val number: String?,
    val series: String?,
    val set: String?,
    val setCode: String?,
    val retreatCost: String?,
    val convertedRetreatCost: String?,
    val text: String?,
    val attackDamage: String?,
    val attackCost: String?,
    val attackName: String?,
    val attackText: String?,
    val abilityName: String?,
    val abilityText: String?,
    val evolvesFrom: String?,
    val contains: String?
) {

    val filter: (Card) -> Boolean
        get() = { card ->
            val filters = mutableListOf<Filter>()

            name?.let { filters += SingularFilter(it, true, Card::name) }
            id?.let { filters += ExactFilter(it, Card::id) }
            nationalPokedexNumber?.let { filters += ExactFilter(it, Card::nationalPokedexNumber) }
            types?.let { filters += MultiplesFilter(it) { it.types ?: emptyList() } }
            subtype?.let { filters += ExactFilter(it, Card::subtype) }
            supertype?.let { filters += ExactFilter(it, Card::supertype) }
            hp?.let { filters += ModifierFilter(it, expand(Card::hp)) }
            number?.let { filters += ExactFilter(it, Card::number) }
            series?.let { filters += ExactFilter(it, Card::series) }
            set?.let { filters += ExactFilter(it, Card::set) }

            // It's important for this to be a partial match since they cards are split by their japanese expansion (i.e. sm12(a|b|c))
            setCode?.let { filters += PartialFilter(it) { listOf(it.setCode) } }

            retreatCost?.let { filters += ModifierFilter(it, expand { it.retreatCost?.size?.toString() ?: "0" }) }
            convertedRetreatCost?.let { filters += ModifierFilter(it, expand { it.retreatCost?.size?.toString() ?: "0" }) }
            text?.let { filters += PartialFilter(it) { listOf(it.text?.joinToString("\n") ?: "") } }
            contains?.let { filters += PartialFilter(it) { listOf(it.text?.joinToString("\n") ?: "") } }
            attackDamage?.let {
                filters += ModifierFilter(it) { it.attacks?.map { it.damage } }
            }
            attackCost?.let {
                filters += ModifierFilter(it) { it.attacks?.map { it.convertedEnergyCost.toString() }}
            }
            attackName?.let { filters += PartialFilter(it) { it.attacks?.map { it.name } ?: emptyList() } }
            attackText?.let { filters += PartialFilter(it) { it.attacks?.map { it.text ?: "" } ?: emptyList() } }
            abilityName?.let { filters += PartialFilter(it) { listOf(it.ability?.name ?: "") } }
            abilityText?.let { filters += PartialFilter(it) { listOf(it.ability?.text ?: "") } }
            evolvesFrom?.let { filters += ExactFilter(it, Card::evolvesFrom) }

            filters.all { it.apply(card) }
        }

    private fun expand(expander: (Card) -> String?): (Card) -> List<String?>? = {
        listOf(expander(it))
    }

    interface Filter {

        fun apply(card: Card): Boolean
    }

    class ExactFilter<T>(
        val input: T,
        val selector: (Card) -> T
    ): Filter {

        override fun apply(card: Card): Boolean {
            return selector(card) == input
        }
    }

    class PartialFilter(
        val input: String,
        val selector: (Card) -> List<String>
    ): Filter {

        override fun apply(card: Card): Boolean {
            return selector(card).any {
                it.contains(input, true)
            }
        }
    }

    class SingularFilter(
        val input: String,
        val allowExactMatch: Boolean = false,
        val selector: (Card) -> String
    ): Filter {

        override fun apply(card: Card): Boolean {
            val orParts = input.split("|")
            val selected = selector(card)
            return orParts.any {
                if (allowExactMatch && QUOTE_REGEX.containsMatchIn(it)) {
                    selected.equals(it.replace("\"", ""), true)
                } else {
                    selected.contains(it, true)
                }
            }
        }

        companion object {
            private val QUOTE_REGEX = "\\\"\\w+\\\"".toRegex()
        }
    }

    class MultiplesFilter(
        val input: String,
        val selector: (Card) -> List<String>
    ): Filter {

        override fun apply(card: Card): Boolean {
            if (input.contains(",")) {
                val andParts = input.split(",")
                val selected = selector(card)
                return andParts.all { part ->
                    selected.all {
                        it.contains(part, true)
                    }
                }
            } else if (input.contains("|")) {
                val orParts = input.split("|")
                val selected = selector(card)
                return orParts.any { part ->
                    selected.all {
                        it.contains(part, true)
                    }
                }
            } else {
                val selected = selector(card)
                return selected.all {
                    it.contains(input, true)
                }
            }
        }
    }

    class ModifierFilter(
        val input: String,
        val selector: (Card) -> List<String?>?
    ): Filter {

        enum class Modifier {
            GT,
            GTE,
            LT,
            LTE,
            NONE;

            companion object {

                fun of(input: String): Modifier {
                    return when {
                        input.contains("gt", true) -> GT
                        input.contains("gte", true) -> GTE
                        input.contains("lt", true) -> LT
                        input.contains("lte", true) -> LTE
                        else -> NONE
                    }
                }
            }
        }

        override fun apply(card: Card): Boolean {
            val modifier = Modifier.of(input)
            val rawInput = NUM_REGEX.find(input)?.value?.toIntOrNull()
            if (rawInput != null) {
                val selected = selector(card)?.filterNotNull()
                if (!selected.isNullOrEmpty()) {
                    return selected.any {
                        val selectedValue = NUM_REGEX.find(it)?.value?.toIntOrNull()
                        if (selectedValue != null) {
                            return when (modifier) {
                                GT -> selectedValue > rawInput
                                GTE -> selectedValue >= rawInput
                                LT -> selectedValue < rawInput
                                LTE -> selectedValue <= rawInput
                                NONE -> selectedValue == rawInput
                            }
                        } else {
                            return false
                        }
                    }
                } else {
                    return false
                }
            } else {
                return false
            }
        }

        companion object {
            val NUM_REGEX = "\\d+".toRegex()
        }
    }
}