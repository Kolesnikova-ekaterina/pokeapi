package com.hfad.myapplication.ui.components.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.hfad.myapplication.viewmodel.PokemonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContent(
    viewModel: PokemonViewModel,
    onApplyFilters: ()->(Unit),
    onResetFilters: ()->(Unit)) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Фильтры", style = MaterialTheme.typography.headlineSmall)


        var isOptionDefault by remember { mutableStateOf("Nevermind") }
        var isOptionMinH by remember { mutableStateOf("") }
        var isOptionMaxH by remember { mutableStateOf("") }

        val options = listOf("Default", "NotDefault", "Nevermind")
        var selectedOption by remember { mutableStateOf(options[0]) }

        Column {
            options.forEach { option ->
                Row(
                    Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = (option == selectedOption),
                        onClick = { selectedOption = option
                            isOptionDefault = option }
                    )
                    Text(text = option)
                }
            }
            Text("Selected: $selectedOption")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text("Min height pokemon")
            TextField(value= isOptionMinH, onValueChange = { isOptionMinH = it})
        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text("Max height pokemon")
            TextField(value= isOptionMaxH, onValueChange = { isOptionMaxH = it})
        }


        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onResetFilters()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сбросить фильтр")
        }
        Button(
            onClick = {
                viewModel.filterOptions.isDefault = isOptionDefault
                if (isOptionMinH.isDigitsOnly() && isOptionMinH.length>0)
                    viewModel.filterOptions.minHeight = isOptionMinH.toInt()
                if (isOptionMaxH.isDigitsOnly() && isOptionMaxH.length>0)
                    viewModel.filterOptions.maxHeight = isOptionMaxH.toInt()
                onApplyFilters()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Применить")
        }
    }
}

