package com.michuu93.pokedex

import android.os.Bundle
import android.support.v4.app.ListFragment
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException

class PokemonListFragment : ListFragment() {
    private lateinit var pokemons: List<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        pokemons = listOf(
            "TEST",
            "LIST"
        )

        loadPokemons()

        context.let {
            adapter = ArrayAdapter(it!!, android.R.layout.simple_list_item_1, pokemons)
        }

    }

    fun loadPokemons() {
        PokemonApiClient().getPokemons(10).enqueue(
            object : ApolloCall.Callback<Pokemons.Data>() {
                override fun onResponse(response: Response<Pokemons.Data>) {
                    val pokemons = response.data()?.pokemons?.map { pokemon ->
                        pokemon.fragments().pokemonSimple
                    }!!

                    Log.d("POKEMONS", pokemons.toString())

                    activity!!.runOnUiThread {
                        val pokemonsOnlyNames = pokemons.map { pokemonSimple -> pokemonSimple.name() }.requireNoNulls().toList()
                        this@PokemonListFragment.pokemons = pokemonsOnlyNames
                        adapter.notifyDataSetChanged()
                        adapter.notifyDataSetInvalidated()
                        Log.d("POKEMONS UPDATED", pokemonsOnlyNames.toString())
                    }
                }

                override fun onFailure(e: ApolloException) {
                    //TODO throw error
                }
            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listAdapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
    }
}