package com.hfad.myapplication.ui.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hfad.myapplication.viewmodel.PokemonViewModel
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Details(
    viewModel: PokemonViewModel,
    navController: NavController,
    pokemonName: String,
){
    viewModel.getCharacter(pokemonName)
    val pokemon by viewModel.pokemonDetail.collectAsState()

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
    ) { contentPadding->
        if (pokemon != null) {
            Box(
                modifier = Modifier.Companion
                    .padding(contentPadding)
                    .background(color = Color.Companion.Gray, shape = RoundedCornerShape(16.dp)
                        )

            ) {
                Column(
                    modifier = Modifier.Companion
                        .padding(5.dp),
                    horizontalAlignment = Alignment.Companion.CenterHorizontally

                ) {
                    DetailText(text = pokemon!!.name.replaceFirstChar { pokemon!!.name[0].uppercaseChar() })
                    DetailText(text = "height = ${pokemon!!.height}")
                    DetailText(
                        text = if (pokemon!!.is_default) {
                            "default"
                        } else {
                            "non default"
                        }
                    )
                    DetailText(text = "base experience:  ${pokemon!!.base_experience}")
                    DetailText(text = "weight = ${pokemon!!.weight}")
                    GlideImage(
                        imageModel = { pokemon!!.sprites.front_default }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailText(text: String){
    Text(
        text = text,
        fontSize = 24.sp
    )
}