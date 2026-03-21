package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Button(onClick = { onBack() }) {
            Text("Retour")
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