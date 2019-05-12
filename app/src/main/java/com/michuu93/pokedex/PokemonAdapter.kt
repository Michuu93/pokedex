package com.michuu93.pokedex

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.michuu93.pokedex.fragment.PokemonSimple

class PokemonAdapter(context: Context, private val pokemons: ArrayList<PokemonSimple>) :
    ArrayAdapter<PokemonSimple>(context, 0, pokemons) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.pokemon_fragment_item, parent, false)!!
        }
        val pokemon = pokemons[position]
        val imageView: ImageView = v.findViewById(R.id.image)!!
        val textView: TextView = v.findViewById(R.id.name)!!

        Glide.with(imageView).load(pokemon.image()).into(imageView)
        textView.text = pokemon.name()

        return v
    }
}