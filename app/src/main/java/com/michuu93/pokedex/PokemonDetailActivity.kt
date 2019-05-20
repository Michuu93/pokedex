package com.michuu93.pokedex

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var pokemon: PokemonDetailed
    private lateinit var dbRepository: PokemonDbRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_detail)

        val pokemonName = intent.getStringExtra("pokemonName")
        Log.d("PokemonDetailActivity", pokemonName)

        viewManager = LinearLayoutManager(this)
        viewAdapter = PokemonViewAdapter(evolutionsList, createOnClickListener(), null, R.layout.pokemon_view_holder)

        evolutionsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        dbRepository = PokemonDbRepository(this)
        pokeballButton.setOnClickListener { onAddToPokeball() }

        loadPokemon(pokemonName)
    }

    private fun loadPokemon(pokemonName: String) {
        detailProgressBar.visibility = View.VISIBLE
        pokemonDatasource.getPokemon(pokemonName) {
            it?.let { pokemon ->
                runOnUiThread {
                    this.pokemon = pokemon
                    setPokemonContent(pokemon)

                    //TODO wait for all evolution requests ends
                    viewAdapter.notifyDataSetChanged()
                    detailProgressBar.visibility = View.GONE
                }
            }
            Log.d("POKEMON", it.toString())
        }
    }

    private fun setPokemonContent(pokemon: PokemonDetailed) {
        Glide.with(this).load(pokemon.image()).into(pokemonDetailImage)
        pokemonDetailName.text = pokemon.name()
        pokemonNumber.text = pokemon.number()

        pokemon.weight()?.let { weight ->
            addPokemonProperty(
                "Weight:",
                weight.minimum().toString() + " - " + weight.maximum().toString()
            )
        }
        pokemon.height()?.let { height ->
            addPokemonProperty(
                "Height:",
                height.minimum().toString() + " - " + height.maximum().toString()
            )
        }
        pokemon.classification()?.let { classification -> addPokemonProperty("Classification:", classification) }
        pokemon.types()?.let { types -> addPokemonProperty("Types:", types.joinToString(", ")) }
        pokemon.resistant()?.let { resistant -> addPokemonProperty("Resistant:", resistant.joinToString(", ")) }

        pokemon.attacks()?.let { attacks ->
            attacks.special()?.let { specialAttacks ->
                addPokemonProperty("Special Attacks:", null)
                specialAttacks.forEach { specialAttack ->
                    addPokemonProperty(
                        specialAttack.name().toString(),
                        specialAttack.type().toString() + " (" + specialAttack.damage().toString() + " dmg)"
                    )
                }
            }
        }

        pokemon.attacks()?.let { attacks ->
            attacks.fast()?.let { fastAttacks ->
                addPokemonProperty("Fast Attacks:", null)
                fastAttacks.forEach { fastAttack ->
                    addPokemonProperty(
                        fastAttack.name().toString(),
                        fastAttack.type().toString() + " (" + fastAttack.damage().toString() + " dmg)"
                    )
                }
            }
        }

        pokemon.fleeRate()?.let { fleeRate -> addPokemonProperty("Flee Rate:", fleeRate.toString()) }
        pokemon.maxCP()?.let { maxCP -> addPokemonProperty("Max CP:", maxCP.toString()) }
        pokemon.maxHP()?.let { maxHP -> addPokemonProperty("Max HP:", maxHP.toString()) }
        pokemon.evolutionRequirements()?.let { evolutionRequirements ->
            addPokemonProperty(
                "Evolution Req:",
                evolutionRequirements.name().toString() + " (" + evolutionRequirements.amount().toString() + ")"
            )
        }

        pokemon.evolutions()?.let { evolutions -> loadEvolutions(evolutions) }
    }

    private fun addPokemonProperty(attribute: String, value: String?) {
        val tableRow = TableRow(this)

        val attributeTextView = TextView(this)
        attributeTextView.text = attribute
        attributeTextView.setTypeface(attributeTextView.typeface, Typeface.BOLD)
        attributeTextView.setPadding(0, 0, 10, 0)
        tableRow.addView(attributeTextView)

        value.let {
            val valueTextView = TextView(this)
            valueTextView.text = value
            tableRow.addView(valueTextView)
        }

        contentTable.addView(tableRow)
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

    private fun onAddToPokeball() {
        pokemon.let {
            val result = dbRepository.insert(it)
            if (result.compareTo(-1) != 0)
                Toast.makeText(this, R.string.pokemon_saved, Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this, R.string.pokemon_saving_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_my_pokeball -> {
                startActivity(Intent(this, MyPokeballActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}