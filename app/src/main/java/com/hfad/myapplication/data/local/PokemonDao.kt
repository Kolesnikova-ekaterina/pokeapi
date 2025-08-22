package com.hfad.myapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemons")
    suspend fun getAllPokemons(): List<CharacterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(character: CharacterEntity)

    @Query("SELECT * FROM pokemons WHERE name = :name")
    suspend fun getPokemonById(name: String): CharacterEntity?

    @Query("DELETE FROM pokemons")
    suspend fun clearAll()
}