package com.michuu93.pokedex

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.michuu93.pokedex.fragment.PokemonDetailed
import com.michuu93.pokedex.fragment.PokemonSimple
import kotlinx.android.synthetic.main.activity_pokemon_detail.*


class PokemonDetailActivity : AppCompatActivity() {
    private var pokemonDatasource: PokemonDatasource = PokemonDatasource(PokemonApiClient())
    private var evolutionsList: ArrayList<PokemonSimple> = arrayListOf()
    private lateinit var viewAdapter: PokemonViewAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_detail)

        val pokemonName = intent.getStringExtra("pokemonName")
        Log.d("PokemonDetailActivity", pokemonName)

        viewManager = LinearLayoutManager(this)
        viewAdapter = PokemonViewAdapter(evolutionsList, null)

        evolutions_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        loadPokemon(pokemonName)
    }

    private fun loadPokemon(pokemonName: String) {
        detail_progress_bar.visibility = View.VISIBLE
        pokemonDatasource.getPokemon(pokemonName) {
            it?.let { pokemon ->
                runOnUiThread {
                    Glide.with(this).load(pokemon.image()).into(image)
                    name.text = pokemon.name()



                    pokemon.evolutions()?.let { evolutions -> loadEvolutions(evolutions) }

                    viewAdapter.notifyDataSetChanged()
                    detail_progress_bar.visibility = View.GONE //TODO wait for all evolution requests end
                }
            }
            Log.d("POKEMON", it.toString())
        }
    }

    private fun loadEvolutions(evolutions: List<PokemonDetailed.Evolution>) {
        evolutions.forEach { it ->
            it.name()?.let { name ->
                pokemonDatasource.getEvolution(name) { evolution ->
                    evolution?.let {
                        evolutionsList.add(it)
                        runOnUiThread { viewAdapter.notifyDataSetChanged() }
                    }
                    Log.d("EVOLUTION", evolution.toString())
                }
            }
        }
    }
}