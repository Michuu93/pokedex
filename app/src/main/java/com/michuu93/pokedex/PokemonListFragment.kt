package com.michuu93.pokedex

import android.os.Bundle
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.View
import android.widget.ListView
import com.michuu93.pokedex.fragment.PokemonSimple

class PokemonListFragment : ListFragment() {
    private var pokemonHelper: PokemonHelper = PokemonHelper(PokemonApiClient())
    private var pokemons: ArrayList<PokemonSimple> = arrayListOf()
    private lateinit var adapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        context.let {
            adapter = PokemonAdapter(it!!, pokemons)
        }
        initialLoad()
    }

    private fun initialLoad() {
        pokemonHelper.getPokemons(10) {
            activity?.runOnUiThread {
                adapter.clear()
                adapter.addAll(it)
                Log.d("POKEMONS", it.toString())
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listAdapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        Log.d("onListItemClick", position.toString() + " - " + pokemons[position].name())
    }
}