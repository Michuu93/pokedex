package com.michuu93.pokedex

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import com.michuu93.pokedex.fragment.PokemonSimple
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.pokemon_view_holder.view.*


class MainActivity : AppCompatActivity() {
    private var pokemonHelper: PokemonHelper = PokemonHelper(PokemonApiClient())
    private var pokemons: ArrayList<PokemonSimple> = arrayListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PokemonViewAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var progressBar: ProgressBar
    private var loadCount = 20
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        viewManager = LinearLayoutManager(this)
        viewAdapter = PokemonViewAdapter(pokemons, createOnClickListener())

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addOnScrollListener(createOnScrollListener())
        }

        progressBar = findViewById(R.id.progress_bar)

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
            Log.d("OnClickListener", it.name.text.toString())
        }
    }

    private fun loadPokemons() {
        loading = true
        progressBar.visibility = View.VISIBLE
        pokemonHelper.getPokemons(loadCount) {
            it?.let { pokemonsList ->
                runOnUiThread {
                    pokemons.clear()
                    pokemons.addAll(pokemonsList)
                    viewAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                    loading = false
                }
            }
            Log.d("POKEMONS", it.toString())
        }
    }
}