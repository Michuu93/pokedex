package com.michuu93.pokedex

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.cache.normalized.CacheKey
import com.apollographql.apollo.cache.normalized.CacheKeyResolver
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import okhttp3.OkHttpClient

class PokemonApiClient {
    companion object {
        const val API_URL = "http://mhoja.me:5000"
//        const val API_URL = " https://graphql-pokemon.now.sh"
    }

    private var cacheFactory: NormalizedCacheFactory<*> =
        LruNormalizedCacheFactory(EvictionPolicy.builder().maxSizeBytes((10 * 1024).toLong()).build())

    private var resolver: CacheKeyResolver = object : CacheKeyResolver() {
        override fun fromFieldRecordSet(field: ResponseField, recordSet: Map<String, Any>): CacheKey {
            return formatCacheKey(recordSet["id"] as String?)
        }

        override fun fromFieldArguments(field: ResponseField, variables: Operation.Variables): CacheKey {
            return formatCacheKey(field.resolveArgument("id", variables) as String?)
        }

        private fun formatCacheKey(id: String?): CacheKey {
            return if (id == null || id.isEmpty()) {
                CacheKey.NO_KEY
            } else {
                CacheKey.from(id)
            }
        }
    }

    private val apolloClient = ApolloClient.builder()
        .serverUrl(API_URL)
        .normalizedCache(cacheFactory, resolver)
        .okHttpClient(OkHttpClient.Builder().build())
        .build()

    fun getPokemons(first: Int): ApolloQueryCall<Pokemons.Data> =
        apolloClient.query(Pokemons.builder().first(first).build())

    fun getPokemon(name: String): ApolloQueryCall<Pokemon.Data> =
        apolloClient.query(Pokemon.builder().name(name).build())

    fun getEvolution(name: String): ApolloQueryCall<Evolution.Data> =
        apolloClient.query(Evolution.builder().name(name).build())
}