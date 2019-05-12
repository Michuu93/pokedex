package com.michuu93.pokedex

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.michuu93.pokedex.fragment.PokemonDetailed
import com.michuu93.pokedex.fragment.PokemonSimple

class PokemonHelper constructor(private val apiClient: PokemonApiClient) {

    fun getPokemons(first: Int, callback: (List<PokemonSimple>?) -> Unit) {
        apiClient.getPokemons(first).enqueue(
            object : ApolloCall.Callback<Pokemons.Data>() {
                override fun onResponse(response: Response<Pokemons.Data>) {
                    callback(response.data()?.pokemons?.map { pokemon -> pokemon.fragments().pokemonSimple })
                }

                override fun onFailure(e: ApolloException) {
                    Log.e("POKEMON API", e.toString())
                }
            })
    }

    fun getPokemon(name: String, callback: (PokemonDetailed?) -> Unit) {
        apiClient.getPokemon(name).enqueue(
            object : ApolloCall.Callback<Pokemon.Data>() {
                override fun onResponse(response: Response<Pokemon.Data>) {
                    callback(response.data()?.pokemon?.fragments()?.pokemonDetailed)
                }

                override fun onFailure(e: ApolloException) {
                    Log.e("POKEMON API", e.toString())
                }
            })
    }
}