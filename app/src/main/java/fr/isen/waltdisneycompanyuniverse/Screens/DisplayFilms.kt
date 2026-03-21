package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.isen.waltdisneycompanyuniverse.datas.Film
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight


/*
Function to take care of displaying a list of films.
It should show :
 [TODO] An image of the Saga (or Franchise) if available.
 */
@Composable
fun DisplayFilms(
    modifier: Modifier,
    films: List<Film>,
    onBack: () -> Unit,
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {

        Button(onClick = { onBack() }) {
            Text("Retour")
        }
        LazyColumn {
            items(films) { film ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(),
                    onClick = {
                        // TODO : Mettre un renvois vers la fonction d'affichage des détails de Nikita.
                    }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "${film.numero}. ${film.titre}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${film.annee} - ${film.genre}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}