package com.hfad.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.myapplication.data.repository.PokemonRepository
import com.hfad.myapplication.domain.Pokemon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PokemonViewModel(
    private val repository: PokemonRepository): ViewModel() {
    private val _pokemons = MutableStateFlow<List<Pokemon>>(emptyList())
    private val _filteredPokemons = MutableStateFlow<List<Pokemon>>(emptyList())
    val filteredPokemons: StateFlow<List<Pokemon>> = _filteredPokemons
    private val _pokemonDetail = MutableStateFlow<Pokemon?>(value = null)
    val pokemonDetail : StateFlow<Pokemon?> = _pokemonDetail
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    val filterOptions= FilterOptions()
    fun ResetFilters(){
        _filteredPokemons.value = _pokemons.value
    }
    fun applyFilters() {
        viewModelScope.launch {
            val baseList = _pokemons.value ?: emptyList()
            val filtered = withContext(Dispatchers.Default) {
                baseList.filter { character ->
                    !filterOptions.isFiltered || (
                            ((character.is_default && filterOptions.isDefault =="Default") ||
                            (!character.is_default && filterOptions.isDefault =="NotDefault")||
                            (filterOptions.isDefault ==("Nevermind"))) &&
                            (character.height >= filterOptions.minHeight) &&
                            (character.height <= filterOptions.maxHeight) &&
                            character.name.contains(filterOptions.name)
                            )
                }
            }
            _filteredPokemons.value = filtered
            Log.e("view model pokemons", "Filter is working")
            Log.e("view model pokemons", filtered.toString())
        }
    }

    fun fetchCharacters() {
        viewModelScope.launch {
            try {
                //val list = RetrofitInstance.api.getCharacters()
                _pokemons.value = repository.getPokemons() ?: emptyList()
                _error.value = null
                applyFilters()
                Log.e("view model pokemons", _pokemons.value.toString())
            } catch (e: Exception) {
                Log.e("CharacterViewModel", "Ошибка загрузки списка персонажей", e)
                _error.value = "Ошибка загрузки списка персонажей"
            }
        }
    }

    fun onLoadMore() {
        viewModelScope.launch {
            val nextPage = repository.getNextPage() ?: emptyList()
            _pokemons.value +=  nextPage
            applyFilters()
        }
    }

    fun onApplyingFilter(){
        viewModelScope.launch {
            applyFilters()
        }
    }

    fun OnSearchName(value: String){
        viewModelScope.launch {
            filterOptions.isFiltered = true
            applyFilters()
        }
    }

    fun getCharacter(name: String) {
        viewModelScope.launch {
            try {
                _pokemonDetail.value = repository.getPokemonDetailes(name = name)
                _error.value = null
                Log.e("view model pokemons", _pokemons.value.toString())

            } catch (e: Exception) {
                Log.e("CharacterViewModel", "Ошибка загрузки персонажа", e)

            }
        }

    }
}

data class FilterOptions(
    var isFiltered: Boolean = false,
    var name: String= "",
    var isDefault: String= "Nevermind",
    var minHeight: Int = Int.MIN_VALUE,
    var maxHeight: Int = Int.MAX_VALUE
)