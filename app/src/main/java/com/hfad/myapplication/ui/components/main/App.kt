package com.hfad.myapplication.ui.components.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.hfad.myapplication.viewmodel.PokemonViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    viewModel: PokemonViewModel,
    navController: NavHostController
){

    val pokemons by viewModel.filteredPokemons.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showFilters by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("PokeAPI")
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .clickable {

                            showFilters = true
                            scope.launch { sheetState.show() }
                        },
                    textAlign = TextAlign.Companion.Center,
                    text = "Открыть фильтры"
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.Companion.padding(contentPadding),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            CustomizableSearchBar(
                onSearch = {
                    viewModel.OnSearchName(it)
                    viewModel.filterOptions.name = it
                })
            CraftGrid(pokemons, viewModel, navController)

        }
        if (showFilters) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        showFilters = false
                    }
                },
                sheetState = sheetState,
            ) {

                FilterContent(
                    viewModel = viewModel,
                    onApplyFilters = {
                        viewModel.filterOptions.isFiltered = true
                        //viewModel.filterOptions.isDefault = isOptionDefault
                        viewModel.onApplyingFilter()
                        scope.launch {
                            sheetState.hide()
                            showFilters = false
                        }
                    },
                    onResetFilters = {
                        viewModel.filterOptions.isFiltered = false
                        //viewModel.filterOptions.isDefault = isOptionDefault
                        viewModel.onApplyingFilter()
                        scope.launch {
                            sheetState.hide()
                            showFilters = false
                        }
                    }
                )
            }
        }
    }
}