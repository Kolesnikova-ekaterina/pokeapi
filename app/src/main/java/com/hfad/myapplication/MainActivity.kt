package com.hfad.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfad.myapplication.data.local.AppDatabase
import com.hfad.myapplication.data.repository.PokemonRepositoryImpl
import com.hfad.myapplication.ui.components.details.Details
import com.hfad.myapplication.ui.components.main.App
import com.hfad.myapplication.viewmodel.PokemonViewModel
import com.hfad.myapplication.viewmodel.PokemonViewModelFactory


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
            MainScreen(viewModel)
        }
    }
}


@Composable
fun MainScreen(viewModel: PokemonViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "App") {
        composable("App") {
            App(viewModel =viewModel,
                navController = navController)
        }
        composable("pokemonDetail/{name}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            Details(
                pokemonName = name!!,
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}
