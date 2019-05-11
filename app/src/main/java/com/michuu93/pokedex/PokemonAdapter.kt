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

class PokemonAdapter(context: Context, private val mDepts: ArrayList<PokemonSimple>) :
    ArrayAdapter<PokemonSimple>(context, 0, mDepts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.pokemon_fragment_item, parent, false)!!
        }
        val dept = mDepts[position]
        val imageView: ImageView = v.findViewById(R.id.image)!!
        val textView: TextView = v.findViewById(R.id.name)!!

        Glide.with(imageView).load(dept.image()).into(imageView)
        textView.text = dept.name()

        return v
    }
}