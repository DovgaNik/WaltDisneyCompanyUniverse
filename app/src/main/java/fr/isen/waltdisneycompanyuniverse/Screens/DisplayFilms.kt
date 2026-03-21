package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.isen.waltdisneycompanyuniverse.datas.Film
import fr.isen.waltdisneycompanyuniverse.datas.statusLabel
import androidx.compose.foundation.lazy.items
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
    filmStatuses: Map<String, String> = emptyMap(),
    onBack: () -> Unit,
    onFilmSelected: (Film) -> Unit,
){
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn {
            items(films) { film ->
                val statusKey = filmStatuses[film.id]
                UnifiedListItemCard(
                    title = "${film.numero}. ${film.titre}",
                    subtitle = "${film.annee} - ${film.genre}",
                    statusText = statusKey?.let { statusLabel(it) },
                    posterTitle = film.titre,
                    onClick = {
                        onFilmSelected(film)
                    }
                )
            }
        }
    }
}