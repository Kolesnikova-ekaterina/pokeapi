package com.hfad.myapplication.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hfad.myapplication.domain.Pokemon
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun PokemonCard(pokemon: Pokemon, onClick: () -> Unit){
    Box(
        modifier = Modifier.Companion
            .padding(5.dp)
            .background(color = Color.Companion.Gray,
                shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.Companion
                .padding(5.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally

        ) {
            GlideImage(
                imageModel = { pokemon.sprites.front_default },
                modifier = Modifier.Companion.fillMaxSize()
            )
            Text(text = pokemon.name.replaceFirstChar { pokemon.name[0].uppercaseChar() })
        }
    }
}