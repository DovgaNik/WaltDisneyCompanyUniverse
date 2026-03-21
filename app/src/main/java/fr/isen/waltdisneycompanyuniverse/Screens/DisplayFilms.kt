package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.isen.waltdisneycompanyuniverse.datas.Film
import fr.isen.waltdisneycompanyuniverse.datas.statusLabel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


/*
Function to take care of displaying a list of films.
It should show :
 [TODO] An image of the Saga (or Franchise) if available.
 */
@Composable
fun DisplayFilms(
    modifier: Modifier,
    films: List<Film>,
    filmStatuses: Map<String, List<String>> = emptyMap(),
    onBack: () -> Unit,
    onFilmSelected: (Film) -> Unit,
){
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        LazyColumn {
            items(films) { film ->
                val statuses = filmStatuses[film.id] ?: emptyList()
                val statusText = if (statuses.isNotEmpty()) {
                    statuses.joinToString(" • ") { statusLabel(it) }
                } else null

                UnifiedListItemCard(
                    title = "${film.numero}. ${film.titre}",
                    subtitle = "${film.annee} - ${film.genre}",
                    statusText = statusText,
                    posterTitle = film.titre,
                    onClick = {
                        onFilmSelected(film)
                    }
                )
            }
        }
    }
}