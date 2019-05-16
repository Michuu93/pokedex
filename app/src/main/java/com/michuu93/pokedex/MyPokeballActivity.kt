package com.michuu93.pokedex

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.michuu93.pokedex.fragment.PokemonSimple
import kotlinx.android.synthetic.main.activity_my_pokeball.*
import kotlinx.android.synthetic.main.pokemon_view_holder.view.*

class MyPokeballActivity : AppCompatActivity() {
    private var pokemons: ArrayList<PokemonSimple> = arrayListOf()
    private lateinit var viewAdapter: PokemonViewAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var dbRepository: PokemonDbRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pokeball)

        viewManager = GridLayoutManager(this, 2)
        viewAdapter = PokemonViewAdapter(pokemons, null, createOnLongClickListener(), R.layout.pokeball_view_holder)

        pokeballRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        dbRepository = PokemonDbRepository(this)
        loadPokemons()
    }

    private fun loadPokemons() {
        val pokemonsFromDb = dbRepository.findAll()
        pokemons.clear()
        pokemons.addAll(pokemonsFromDb)
        viewAdapter.notifyDataSetChanged()
        viewAdapter.notifyDataSetChanged()
    }

    private fun createOnLongClickListener(): View.OnLongClickListener {
        return View.OnLongClickListener {
            val pokemonName = it.pokemonSimpleName.text.toString()
            Log.d("OnLongClickListener", pokemonName)
            val message = getString(R.string.do_you_want_delete) + " " + pokemonName + "?"

            AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    val result = dbRepository.delete(pokemonName)
                    if (result == 1) {
                        this.pokemons.remove(this.pokemons.first { p -> p.name().equals(pokemonName) })
                        runOnUiThread { viewAdapter.notifyDataSetChanged() }
                    }
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                    //do nothing
                }
                .create()
                .show()
            true
        }
    }
}