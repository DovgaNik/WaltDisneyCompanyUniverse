package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.isen.waltdisneycompanyuniverse.datas.SearchIndexEntry
import fr.isen.waltdisneycompanyuniverse.datas.SearchParameter
import java.text.Normalizer
import java.util.Locale

private fun normalizeSearchText(value: String): String {
    val withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
    return withoutAccents.lowercase(Locale.ROOT).trim()
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    entries: List<SearchIndexEntry>,
    onFilmSelected: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedParameter by remember { mutableStateOf(SearchParameter.All) }

    val normalizedQuery = normalizeSearchText(query)
    val results = remember(entries, normalizedQuery, selectedParameter) {
        val filtered = entries.filter { entry ->
            val haystack = when (selectedParameter) {
                SearchParameter.All -> listOf(entry.filmTitle, entry.franchise, entry.saga, entry.category)
                SearchParameter.Film -> listOf(entry.filmTitle)
                SearchParameter.Franchise -> listOf(entry.franchise)
                SearchParameter.Saga -> listOf(entry.saga)
                SearchParameter.Category -> listOf(entry.category)
            }

            normalizedQuery.isBlank() || haystack.any { normalizeSearchText(it).contains(normalizedQuery) }
        }

        filtered.sortedWith(compareBy<SearchIndexEntry> { it.filmTitle }.thenBy { it.year })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            label = { Text("Search", color = Color.White) },
            placeholder = { Text("Search by film, franchise, saga...", color = Color.White.copy(alpha = 0.6f)) },
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                cursorColor = Color.White
            )
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchParameter.entries.forEach { parameter ->
                FilterChip(
                    selected = selectedParameter == parameter,
                    onClick = { selectedParameter = parameter },
                    label = { Text(parameter.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        labelColor = Color.White,
                        selectedLabelColor = Color(0xFF070222),
                        selectedContainerColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedParameter == parameter,
                        borderColor = Color.White.copy(alpha = 0.5f),
                        selectedBorderColor = Color.White
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (results.isEmpty()) {
                item {
                    Text(
                        text = if (normalizedQuery.isBlank()) {
                            "Type something to search."
                        } else {
                            "No result found."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            } else {
                items(results, key = { it.filmId }) { result ->
                    UnifiedListItemCard(
                        title = result.filmTitle,
                        subtitle = "${result.year} - ${result.genre}",
                        statusText = "${result.franchise} - ${result.saga}",
                        posterTitle = result.filmTitle,
                        onClick = { onFilmSelected(result.filmId) }
                    )
                }
            }
        }
    }
}
