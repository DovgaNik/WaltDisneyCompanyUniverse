package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.isen.waltdisneycompanyuniverse.datas.Film
import kotlin.collections.forEach
import androidx.compose.foundation.lazy.items


/*
Function to take care of displaying a list of films.
It should show :
 [TODO] An image of the Saga (or Franchise) if available.
 */
@Composable
fun DisplayFilms(modifier: Modifier, work_in_progress_image: String?, films: List<Film>){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        items(films) { film ->
            Card(
                onClick = {
                    // TODO : Mettre un renvois vers la fonction d'affichage des détails de Nikita.
                }
            ) {
                Text("Work in progress.")
            }
        }
    }
}