package com.michuu93.pokedex

import android.content.Intent
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
import kotlinx.android.synthetic.main.pokemon_view_holder.view.*


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
        viewAdapter = PokemonViewAdapter(evolutionsList, createOnClickListener())

        evolutionsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        loadPokemon(pokemonName)
    }

    private fun loadPokemon(pokemonName: String) {
        detailProgressBar.visibility = View.VISIBLE
        pokemonDatasource.getPokemon(pokemonName) {
            it?.let { pokemon ->
                runOnUiThread {
                    Glide.with(this).load(pokemon.image()).into(pokemonDetailImage)
                    pokemonDetailName.text = pokemon.name()
                    content.text = pokemon.toString() //TODO content layout
                    pokemon.evolutions()?.let { evolutions -> loadEvolutions(evolutions) }

                    //TODO wait for all evolution requests end
                    viewAdapter.notifyDataSetChanged()
                    detailProgressBar.visibility = View.GONE
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

    private fun createOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            val pokemonName = it.pokemonSimpleName.text.toString()
            Log.d("OnClickListener", pokemonName)
            val intent = Intent(this, PokemonDetailActivity::class.java)
            intent.putExtra("pokemonName", pokemonName)
            startActivity(intent)
        }
    }
}