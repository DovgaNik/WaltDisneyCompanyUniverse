package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import fr.isen.waltdisneycompanyuniverse.datas.Film
import fr.isen.waltdisneycompanyuniverse.datas.SousSaga
import androidx.compose.ui.unit.dp

/*
This function is intended to display the saas of a franchise.
It should provide with a display consisting of:
 [TODO] Listing a set amount of sagas.
 [TODO] Displaying
 */
@Composable
fun DisplaySagas(
    modifier: Modifier = Modifier,
    sagas: List<SousSaga>,
    onBack: () -> Unit,
    onFilmClick: (List<Film>) -> Unit){
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
            items(sagas) { saga ->
                UnifiedListItemCard(
                    title = saga.nom,
                    subtitle = "${saga.films.size} films",
                    onClick = { onFilmClick(saga.films) }
                )
            }
        }
    }
}