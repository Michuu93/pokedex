package com.michuu93.pokedex

import android.os.Bundle
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.View
import android.widget.ListView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.michuu93.pokedex.fragment.PokemonSimple

class PokemonListFragment : ListFragment() {
    private var pokemons: ArrayList<PokemonSimple> = arrayListOf()
    private lateinit var adapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        context.let {
            adapter = PokemonAdapter(it!!, pokemons)
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
                        adapter.clear()
                        adapter.addAll(pokemons)
                        Log.d("POKEMONS UPDATED", pokemons.toString())
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