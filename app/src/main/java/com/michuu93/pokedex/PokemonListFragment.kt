package com.michuu93.pokedex

import android.os.Bundle
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlin.collections.ArrayList

class PokemonListFragment : ListFragment() {
    private var pokemons: ArrayList<String> = arrayListOf()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        context.let {
            adapter = ArrayAdapter(it!!, android.R.layout.simple_list_item_1, pokemons)
        }

        loadPokemons()
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        Log.d("onListItemClick", position.toString() + " - " + pokemons[position])
    }

    private fun loadPokemons() {
        PokemonApiClient().getPokemons(100).enqueue(
            object : ApolloCall.Callback<Pokemons.Data>() {
                override fun onResponse(response: Response<Pokemons.Data>) {
                    val pokemons = response.data()?.pokemons?.map { pokemon ->
                        pokemon.fragments().pokemonSimple
                    }!!
                    activity!!.runOnUiThread {
                        val pokemonsOnlyNames = pokemons.map { pokemonSimple -> pokemonSimple.name() }.requireNoNulls().toList()
                        adapter.clear()
                        adapter.addAll(pokemonsOnlyNames)
                        Log.d("POKEMONS UPDATED", pokemonsOnlyNames.toString())
                    }
                }

                override fun onFailure(e: ApolloException) {
                    Log.e("POKEMON API", e.toString())
                }
            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listAdapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
    }
}