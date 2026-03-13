package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.isen.waltdisneycompanyuniverse.datas.Film

/*
Function used when the film is clicked.
Should display:
 - It's image at the top of the page.
 - Information about the film.
 - Buttons to toggle that we own the film, watched the film, want to get rid of it, show who wants to get rid of it.
 - Maybe show how many peoples own that film?
*/
@Composable
fun DisplayFilmDetails(modifier: Modifier, film: Film){
    Column() {
        Text("Afficher informations ICI.")
    }
}