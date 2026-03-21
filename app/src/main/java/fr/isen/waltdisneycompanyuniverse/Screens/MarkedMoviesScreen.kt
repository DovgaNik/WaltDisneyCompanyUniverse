package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.isen.waltdisneycompanyuniverse.datas.Film
import fr.isen.waltdisneycompanyuniverse.datas.statusLabel

@Composable
fun MarkedMoviesScreen(
    modifier: Modifier = Modifier,
    markedSections: List<Pair<String, List<Film>>>,
    onFilmSelected: (Film) -> Unit
) {
    if (markedSections.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "You haven't marked any movie yet.",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        markedSections.forEach { (statusKey, films) ->
            item(key = "header_$statusKey") {
                Text(
                    text = statusLabel(statusKey),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            items(
                items = films,
                key = { film -> "${statusKey}_${film.id}" }
            ) { film ->
                UnifiedListItemCard(
                    title = "${film.numero}. ${film.titre}",
                    subtitle = "${film.annee} - ${film.genre}",
                    statusText = statusLabel(statusKey),
                    posterTitle = film.titre,
                    onClick = { onFilmSelected(film) }
                )
            }
        }
    }
}

