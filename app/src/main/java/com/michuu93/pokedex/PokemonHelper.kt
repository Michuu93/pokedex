package com.michuu93.pokedex

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.michuu93.pokedex.fragment.PokemonSimple

class PokemonHelper {

    fun getPokemons(first: Int): List<PokemonSimple> {
        var pokemons: List<PokemonSimple> = emptyList()
        PokemonApiClient().getPokemons(first).enqueue(
            object : ApolloCall.Callback<Pokemons.Data>() {
                override fun onResponse(response: Response<Pokemons.Data>) {
                    pokemons = response.data()?.pokemons?.map { pokemon ->
                        pokemon.fragments().pokemonSimple
                    }!!

                    Log.d("POKEMONS", pokemons.toString())
                }

                override fun onFailure(e: ApolloException) {
                    //throw error
                }
            })

        return pokemons
    }

    //
//    private fun drawPokemon(name: String?) {
//        PokemonApiClient().getPokemon(name!!).enqueue(
//            object : ApolloCall.Callback<Pokemon.Data>() {
//                override fun onResponse(response: Response<Pokemon.Data>) {
//                    val pokemon = response.data()?.pokemon
//                    Log.d("POKEMON", pokemon?.toString())
//                    val textView = findViewById<View>(R.id.name) as TextView
//                    setText(textView, pokemon?.toString())
//                    val imageView = findViewById<View>(R.id.image) as ImageView
//                    runOnUiThread{ Glide.with(imageView).loadPokemons(     pokemon?.fragments()?.pokemonDetailed?.image()   ).into(imageView)}
//                }
//                override fun onFailure(e: ApolloException) {
//                    //throw error
//                }
//            }
//        )
//    }

//    private fun setText(text: TextView, value: String?) {
//        runOnUiThread { text.text = value }
//    }

}