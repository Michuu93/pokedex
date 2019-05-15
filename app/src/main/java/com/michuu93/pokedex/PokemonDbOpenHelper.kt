package com.michuu93.pokedex

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.michuu93.pokedex.PokemonDbContract.PokemonEntry.Companion.ID
import com.michuu93.pokedex.PokemonDbContract.PokemonEntry.Companion.IMAGE
import com.michuu93.pokedex.PokemonDbContract.PokemonEntry.Companion.NAME
import com.michuu93.pokedex.PokemonDbContract.PokemonEntry.Companion.NUMBER
import com.michuu93.pokedex.PokemonDbContract.PokemonEntry.Companion.POKEMON_TABLE_NAME
import org.jetbrains.anko.db.*

class PokemonDbOpenHelper private constructor(ctx: Context) :
    ManagedSQLiteOpenHelper(ctx, POKEMON_TABLE_NAME, null, 1) {
    init {
        instance = this
    }

    companion object {
        private var instance: PokemonDbOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context) = instance ?: PokemonDbOpenHelper(ctx.applicationContext)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(
            POKEMON_TABLE_NAME, true,
            ID to TEXT + PRIMARY_KEY + UNIQUE,
            NUMBER to TEXT + NOT_NULL,
            NAME to TEXT + NOT_NULL,
            IMAGE to TEXT + NOT_NULL
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(POKEMON_TABLE_NAME, true)
    }
}

val Context.db: PokemonDbOpenHelper
    get() = PokemonDbOpenHelper.getInstance(this)