package com.hfad.myapplication.ui.components.main

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizableSearchBar(
    onSearch: (String) -> Unit,
    placeholder: @Composable () -> Unit = { Text("Search") },
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(
            Icons.Default.Search,
            contentDescription = "Search"
        )
    },
    trailingIcon: @Composable (() -> Unit)? ={
        Icon(
            Icons.Default.MoreVert,
            contentDescription = "MoreFilters"
        )
    },
    modifier: Modifier = Modifier.Companion
) {
    // Track expanded state of search bar
    var expanded by rememberSaveable { mutableStateOf(false) }
    var queryline by remember { mutableStateOf("") }

    Box(
        modifier
            .semantics { isTraversalGroup = true },

        ) {
        SearchBar(
            modifier = Modifier.Companion
                .align(Alignment.Companion.TopCenter)
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