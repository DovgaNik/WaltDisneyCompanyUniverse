package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.isen.waltdisneycompanyuniverse.datas.Film
import fr.isen.waltdisneycompanyuniverse.datas.SousSaga
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.font.FontWeight
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
            .statusBarsPadding()
    ) {

        Button(onClick = { onBack() }) {
            Text("Retour")
        }
        LazyColumn {
            items(sagas) { saga ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(),
                    onClick = {
                        onFilmClick(saga.films)
                    }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = saga.nom,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${saga.films.size} films",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}