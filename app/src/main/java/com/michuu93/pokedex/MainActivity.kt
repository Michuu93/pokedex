package com.michuu93.pokedex

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.michuu93.pokedex.fragment.PokemonSimple
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity() {
    private var pokemonHelper: PokemonHelper = PokemonHelper(PokemonApiClient())
    private var pokemons: ArrayList<PokemonSimple> = arrayListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PokemonViewAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var loadCount = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        viewManager = LinearLayoutManager(this)
        viewAdapter = PokemonViewAdapter(pokemons)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
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
                    loadCount += 10
                    loadPokemons()
                }
            }
        }
    }

    private fun loadPokemons() {
        pokemonHelper.getPokemons(loadCount) {
            it?.let { pokemonsList ->
                runOnUiThread {
                    pokemons.clear()
                    pokemons.addAll(pokemonsList)
                    viewAdapter.notifyDataSetChanged()
                }
            }
            Log.d("POKEMONS", it.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}