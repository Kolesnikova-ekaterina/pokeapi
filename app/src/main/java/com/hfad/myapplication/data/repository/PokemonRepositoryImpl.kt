package com.hfad.myapplication.data.repository

import android.content.Context
import android.util.Log
import com.hfad.myapplication.data.local.AppDatabase
import com.hfad.myapplication.data.local.CharacterEntity
import com.hfad.myapplication.data.local.PokemonDao
import com.hfad.myapplication.data.remote.NetworkUtils
import com.hfad.myapplication.data.remote.RetrofitInstance
import com.hfad.myapplication.domain.ApiData
import com.hfad.myapplication.domain.Pokemon
import com.hfad.myapplication.domain.Sprite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.awaitAll

interface PokemonRepository{
    suspend fun getPokemons(): List<Pokemon>?
    suspend fun getNextPage(): List<Pokemon>?
    suspend fun getPokemonDetailes(name: String): Pokemon?
}

class PokemonRepositoryImpl(
        private val db: AppDatabase,
        private val context: Context
    ): PokemonRepository {
    private val retrinstance = RetrofitInstance
    private val api = retrinstance.api
    private val dao = db.pokemonDao()
    private lateinit var apiData: ApiData
    private var offset = 0
    private var limit = 20

    override suspend fun getPokemons(): List<Pokemon>? = withContext(Dispatchers.IO) {
        if(!NetworkUtils.isNetworkAvailable(context))
            return@withContext getCachedCharacterList()

        return@withContext fetchAndCacheCharacters()
    }

    private suspend fun getCachedCharacterList(): List<Pokemon>? {
        return dao.getAllPokemons().map{item -> item.toDomain()}
    }

    private suspend fun fetchAndCacheCharacters(): List<Pokemon> {
        apiData = api.getPokemons()
        val _remoteList = apiData.results
        Log.e("fetchAndCacheCharacters  ", _remoteList.toString() )
        var remoteList = emptyList<Pokemon>()
        _remoteList.forEach { item ->
            //Log.e("fetchAndCacheCharacters  ",item.toString() )
            val details = api.getPokemonDetails(item.name)
            dao.insertPokemon(details.toEntity())
            //Log.e("fetchAndCacheCharacters  ",details.toString() )
            remoteList = remoteList.plus(details)
        }
        //Log.e("remote list length", remoteList.size.toString())
        return remoteList
    }

    private fun Pokemon.toEntity() = CharacterEntity(
        id = id,
        name = name,
        base_experience = base_experience,
        height = height,
        is_default = is_default,
        order = order,
        weight = weight,
        imageurl = sprites.front_default,
    )
    private fun CharacterEntity.toDomain() = Pokemon(
        id = id,
        name = name,
        base_experience = base_experience,
        height = height,
        is_default = is_default,
        order = order,
        weight = weight,
        sprites = Sprite(front_default = imageurl),
    )

    override suspend fun getNextPage(): List<Pokemon>?=withContext(Dispatchers.IO) {
        offset += limit
        apiData = api.getPage(offset, limit)
        val _remoteList = apiData.results
        Log.e("fetch   ", _remoteList.toString() )
        val deferredDetails = coroutineScope {
            _remoteList.map { item ->
                async {
                    val details = api.getPokemonDetails(item.name)
                    dao.insertPokemon(details.toEntity())
                    details
                }
            }
        }

        deferredDetails.awaitAll()
    }

    override suspend fun getPokemonDetailes(name: String): Pokemon?  = withContext(Dispatchers.IO){
        if(!NetworkUtils.isNetworkAvailable(context))
            return@withContext getCachedCharacter(name)

        return@withContext fetchAndCacheCharacter(name)
    }

    private suspend fun getCachedCharacter(name: String): Pokemon? {
        return dao.getPokemonById(name = name)?.toDomain()
    }

    private suspend fun fetchAndCacheCharacter(name: String): Pokemon? {
        val pokemon = api.getPokemonDetails(name)
        dao.insertPokemon(pokemon.toEntity())
        return pokemon
    }
}