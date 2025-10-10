package com.hfad.myapplication.ui.components.main

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfad.myapplication.domain.Pokemon
import com.hfad.myapplication.viewmodel.PokemonViewModel

@Composable
fun CraftGrid(
    pokemons: List<Pokemon>,
    viewModel: PokemonViewModel,
    navController: NavHostController
){
    val listState = rememberLazyGridState()
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= pokemons.size - 3) {
                    Log.e("LOAD MORE MAIN ACTIVITY", "load more pokemons")
                    viewModel.onLoadMore()
                }
            }
    }
    LazyVerticalGrid(
        modifier = Modifier.Companion.padding(20.dp),
        columns = GridCells.Fixed(2),
        state = listState
    ) {
        items(pokemons, key = { it.id }) { p ->
            PokemonCard(pokemon = p,
                onClick = {
                    navController.navigate("pokemonDetail/${p.name}")
                })
        }

    }
}