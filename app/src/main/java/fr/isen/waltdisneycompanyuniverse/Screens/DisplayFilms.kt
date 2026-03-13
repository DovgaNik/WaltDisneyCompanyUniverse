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

/*
Function to take care of displaying a list of films.
It should show :
 [TODO] An image of the Saga (or Franchise) if available.
 [TODO] A set amount of film per page.
 [TODO] A system to switch between display pages.
 */
@Composable
fun DisplayFilms(modifier: Modifier, work_in_progress_image: String?, films: List<Film>){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        films.forEach { film ->
            Card(
                onClick = {
                    DisplayFilmDetails(Modifier, film)
                }
            ) {
                Text("Work in progress.")
            }
        }

        // Selecting page bar.
        /*if(films.size > max_films_per_page){

        }*/
    }
}