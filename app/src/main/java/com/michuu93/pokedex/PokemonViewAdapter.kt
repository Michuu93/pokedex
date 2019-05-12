package com.michuu93.pokedex

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.michuu93.pokedex.fragment.PokemonSimple
import kotlinx.android.synthetic.main.pokemon_view_holder.view.*

class PokemonViewAdapter(private val pokemons: ArrayList<PokemonSimple>) :
    RecyclerView.Adapter<PokemonViewAdapter.PokemonViewHolder>() {

    class PokemonViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PokemonViewHolder =
        PokemonViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pokemon_view_holder, parent, false))

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemons[position]
        Glide.with(holder.itemView.context).load(pokemon.image()).into(holder.itemView.image)
        holder.itemView.name.text = pokemon.name()
    }

    override fun getItemCount(): Int {
        return pokemons.size
    }
}