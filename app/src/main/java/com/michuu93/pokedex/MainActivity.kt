package com.michuu93.pokedex

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.michuu93.pokedex.fragment.PokemonSimple
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.pokemon_view_holder.view.*


class MainActivity : AppCompatActivity() {
    private var pokemonDatasource: PokemonDatasource = PokemonDatasource(PokemonApiClient())
    private var pokemons: ArrayList<PokemonSimple> = arrayListOf()
    private lateinit var viewAdapter: PokemonViewAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var loadCount = 20
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewManager = LinearLayoutManager(this)
        viewAdapter = PokemonViewAdapter(pokemons, createOnClickListener())

        pokemons_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addOnScrollListener(createOnScrollListener())
        }

        loadPokemons()
    }

    private fun createOnScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if (!loading) {
                        loadCount += 20
                        loadPokemons()
                    }
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

    private fun loadPokemons() {
        loading = true
        list_progress_bar.visibility = View.VISIBLE
        pokemonDatasource.getPokemons(loadCount) {
            it?.let { pokemonsList ->
                runOnUiThread {
                    pokemons.clear()
                    pokemons.addAll(pokemonsList)
                    viewAdapter.notifyDataSetChanged()
                    list_progress_bar.visibility = View.GONE
                    loading = false
                }
            }
            Log.d("POKEMONS", it.toString())
        }
    }
}