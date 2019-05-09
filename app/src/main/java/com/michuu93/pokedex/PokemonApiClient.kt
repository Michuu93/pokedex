package com.michuu93.pokedex

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.cache.normalized.CacheKey
import com.apollographql.apollo.cache.normalized.CacheKeyResolver
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient


class PokemonApiClient {
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
        .serverUrl("https://graphql-pokemon.now.sh")
        .normalizedCache(cacheFactory, resolver)
        .okHttpClient(OkHttpClient.Builder().build())
        .build()

    fun getPokemons(first: Int): Single<Pokemons.Data>? =
        Rx2Apollo.from(apolloClient.query(Pokemons.builder().first(first).build()))
            .subscribeOn(Schedulers.io())
            .map { it.toData() }
            .singleOrError()

    private fun <T> com.apollographql.apollo.api.Response<T>.toData(): T =
        if (hasErrors()) throw ResponseException(errors())
        else data() ?: throw NullPointerException()

    class ResponseException(errors: List<com.apollographql.apollo.api.Error>) : Exception(errors.toString())
}