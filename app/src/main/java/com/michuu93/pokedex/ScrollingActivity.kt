package com.michuu93.pokedex

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.android.synthetic.main.activity_scrolling.*
import android.view.View
import com.bumptech.glide.Glide
import android.widget.ImageView

class ScrollingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        PokemonApiClient().getPokemons(1).enqueue(
                object : ApolloCall.Callback<Pokemons.Data>() {
                    override fun onResponse(response: Response<Pokemons.Data>) {
                        val pokemons = response.data()?.pokemons
                        Log.d("POKEMONS", pokemons?.toString())
                        val name = pokemons?.get(0)?.fragments()?.pokemonSimple?.name()
                        drawPokemon(name)
                    }
                    override fun onFailure(e: ApolloException) {
                        //throw error
                    }
                }
            )
    }

    private fun drawPokemon(name: String?) {
        PokemonApiClient().getPokemon(name!!).enqueue(
            object : ApolloCall.Callback<Pokemon.Data>() {
                override fun onResponse(response: Response<Pokemon.Data>) {
                    val pokemon = response.data()?.pokemon
                    Log.d("POKEMON", pokemon?.toString())
                    val textView = findViewById<View>(R.id.name) as TextView
                    setText(textView, pokemon?.toString())
                    val imageView = findViewById<View>(R.id.image) as ImageView
                    runOnUiThread{ Glide.with(imageView).load(     pokemon?.fragments()?.pokemonDetailed?.image()   ).into(imageView)}
                }
                override fun onFailure(e: ApolloException) {
                    //throw error
                }
            }
        )
    }

    private fun setText(text: TextView, value: String?) {
        runOnUiThread { text.text = value }
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
