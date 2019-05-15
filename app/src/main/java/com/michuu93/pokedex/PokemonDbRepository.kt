package com.michuu93.pokedex

import android.content.Context
import com.michuu93.pokedex.PokemonDbContract.PokemonEntry.Companion.ID
import com.michuu93.pokedex.PokemonDbContract.PokemonEntry.Companion.IMAGE
import com.michuu93.pokedex.PokemonDbContract.PokemonEntry.Companion.NAME
import com.michuu93.pokedex.PokemonDbContract.PokemonEntry.Companion.NUMBER
import com.michuu93.pokedex.PokemonDbContract.PokemonEntry.Companion.POKEMON_TABLE_NAME
import com.michuu93.pokedex.fragment.PokemonSimple
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.*

class PokemonDbRepository(val ctx: Context) {
    fun findAll(): ArrayList<PokemonSimple> = ctx.db.use {
        val pokemons = ArrayList<PokemonSimple>()

        select(ID, NUMBER, NAME, IMAGE).parseList(object : MapRowParser<List<PokemonSimple>> {
            override fun parseRow(columns: Map<String, Any?>): List<PokemonSimple> {
                val id = columns.getValue(ID)
                val number = columns.getValue(NUMBER)
                val name = columns.getValue(NAME)
                val image = columns.getValue(IMAGE)

                val pokemon = PokemonSimple(
                    "PokemonSimple",
                    id.toString(),
                    number.toString(),
                    name.toString(),
                    image.toString()
                )

                pokemons.add(pokemon)
                return pokemons
            }
        })
        pokemons
    }

    fun create(pokemon: PokemonSimple) = ctx.db.use {
        insert(
            POKEMON_TABLE_NAME,
            ID to pokemon.id(),
            NUMBER to pokemon.number(),
            NAME to pokemon.name(),
            IMAGE to pokemon.image()
        )
    }

    fun update(pokemon: PokemonSimple) = ctx.db.use {
        update(
            POKEMON_TABLE_NAME,
            NUMBER to pokemon.number(),
            NAME to pokemon.name(),
            IMAGE to pokemon.image()
        )
            .whereArgs("id = {id}", ID to pokemon.id())
            .exec()
    }

    fun delete(pokemon: PokemonSimple) = ctx.db.use {
        delete(
            POKEMON_TABLE_NAME,
            "id = {id}",
            ID to pokemon.id()
        )
    }
}