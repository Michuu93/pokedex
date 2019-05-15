package com.michuu93.pokedex

import android.provider.BaseColumns

object PokemonDbContract {
    class PokemonEntry : BaseColumns {
        companion object {
            const val POKEMON_TABLE_NAME = "POKEMONS"
            const val ID = "id"
            const val NUMBER = "number"
            const val NAME = "name"
            const val IMAGE = "image"
        }
    }
}