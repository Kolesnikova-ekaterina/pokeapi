package com.hfad.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hfad.myapplication.data.local.AppDatabase
import com.hfad.myapplication.data.repository.PokemonRepository
import com.hfad.myapplication.data.repository.PokemonRepositoryImpl
import com.hfad.myapplication.domain.Pokemon
import com.hfad.myapplication.ui.theme.MyApplicationTheme
import com.hfad.myapplication.viewmodel.PokemonViewModel
import com.hfad.myapplication.viewmodel.PokemonViewModelFactory
import kotlinx.coroutines.processNextEventInCurrentThread
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import com.hfad.myapplication.ui.components.FilterContent


class MainActivity : ComponentActivity() {
    private lateinit var viewModel: PokemonViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getInstance(applicationContext)
        val repository = PokemonRepositoryImpl(database, applicationContext)
        val factory = PokemonViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[PokemonViewModel::class.java]
        viewModel.fetchCharacters()
        setContent {
            App(viewModel)
        }
    }
}

@Composable
fun PokemonCard(pokemon: Pokemon){
    Box (
        modifier = Modifier
            .padding(5.dp)
            .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))

    ){
        Column(
            modifier = Modifier
            .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ){
            GlideImage(
                imageModel = {pokemon.sprites.front_default},
                modifier = Modifier.fillMaxSize()
            )
            Text(text=pokemon.name.replaceFirstChar { pokemon.name[0].uppercaseChar() })
        }
    }
}


@Composable
fun CraftGrid(
    pokemons: List<Pokemon>,
    viewModel: PokemonViewModel
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
        modifier = Modifier.padding(20.dp),
        columns = GridCells.Fixed(2),
        state = listState
    ) {
        items(pokemons, key = { it.id }) { p ->
            PokemonCard(p)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    viewModel: PokemonViewModel
){

    val pokemons by viewModel.filteredPokemons.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showFilters by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            showFilters = true
                            scope.launch { sheetState.show() }
                        },
                    textAlign = TextAlign.Center,
                    text = "Открыть фильтры"
                )
            }
        }
    ) {contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomizableSearchBar(
                onSearch = {viewModel.OnSearchName(it)
                    viewModel.filterOptions.name = it})
            CraftGrid(pokemons, viewModel)

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
                    onApplyFilters ={
                        viewModel.filterOptions.isFiltered = true
                        //viewModel.filterOptions.isDefault = isOptionDefault
                        viewModel.onApplyingFilter()
                        scope.launch {
                            sheetState.hide()
                            showFilters = false
                        }
                    },
                    onResetFilters ={
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizableSearchBar(
    onSearch: (String) -> Unit,
    placeholder: @Composable () -> Unit = { Text("Search") },
    leadingIcon: @Composable (() -> Unit)? = { Icon(Icons.Default.Search, contentDescription = "Search") },
    trailingIcon: @Composable (() -> Unit)? ={ Icon(Icons.Default.MoreVert, contentDescription = "MoreFilters") },
    modifier: Modifier = Modifier
) {
    // Track expanded state of search bar
    var expanded by rememberSaveable { mutableStateOf(false) }
    var queryline by remember { mutableStateOf("") }

    Box(
        modifier
            .semantics { isTraversalGroup = true },

    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                // Customizable input field implementation
                SearchBarDefaults.InputField(
                    query = queryline,
                    onQueryChange = {
                        queryline = it
                    },
                    onSearch = {
                        onSearch(queryline)
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {

        }
    }
}

