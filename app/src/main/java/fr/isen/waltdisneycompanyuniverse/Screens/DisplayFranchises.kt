package fr.isen.waltdisneycompanyuniverse.Screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import fr.isen.waltdisneycompanyuniverse.datas.*

/*
Function to set up the connection to the database.
 */
@Composable
fun Prologue(modifier: Modifier = Modifier) {
    Log.d("DEBUG", "Prologue activé !")

    val ref = FirebaseDatabase
        .getInstance()
        .getReference(categories)

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    LaunchedEffect(Unit) {

        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val loadedCategories = mutableListOf<Category>()

                snapshot.children.forEach { categorySnapshot ->
                    val category = categorySnapshot.getValue(Category::class.java)
                    category?.let { loadedCategories.add(it) }
                }

                categories = loadedCategories
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erreur lecture", error.toException())
            }

        })
    }
    var selectedFilms by remember { mutableStateOf<List<Film>>(emptyList<Film>()) }
    var selectedSaga by remember { mutableStateOf<List<SousSaga>>(emptyList<SousSaga>()) }

    if (selectedSaga == emptyList<SousSaga>() && selectedFilms == emptyList<Film>()){
        Log.d("debug", "selectedFranchise == null")
        DisplayFranchises(
            modifier,
            ref,
            categories,
            onFranchiseFilmsClick = { films ->
                selectedFilms = films
            },
            onFanchiseSousSagaClick = { sagas ->
                selectedSaga = sagas
            }
        )
    }
    // Dans le cas où notre liste de films à afficher n'est pas vide.
    else if (selectedFilms != emptyList<Film>()) {
        Log.d("debug", "selectedFranchise (films) : $selectedFilms")
        DisplayFilms(
            modifier,
            selectedFilms,
            onBack = {  // Si l'utilisateur décide de retourner en arrière, nous n'avons plus à lui afficher les films.
                selectedFilms = emptyList<Film>()
            }
        )
    }
    // Si nous avons sélectionné une saga.
    else if (selectedSaga != emptyList<SousSaga>()){
        Log.d("debug", "selectedFranchise (sous saga) : $selectedSaga")
        DisplaySagas(
            modifier,
            selectedSaga,
            onBack = {  // Si l'utilisateur décide de revenir en arrière, nous n'avons plus à afficher les sagas.
                selectedSaga = emptyList<SousSaga>()
            },
            onFilmClick = { films ->
                selectedFilms = films
            }
        )
    }
}


@Composable
fun DisplayFranchises(modifier: Modifier = Modifier, ref: DatabaseReference, categories: List<Category>, onFranchiseFilmsClick: (List<Film>) -> Unit, onFanchiseSousSagaClick: (List<SousSaga>) -> Unit) {
    Log.d("DEBUG", "DisplayListOfFaS activé !")
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        categories.forEach { categorie ->
            items(categorie.franchises) { franchise ->
                val subtitle = if (franchise.sous_sagas.isNotEmpty()) {
                    "${franchise.sous_sagas.size} sagas"
                } else {
                    "${franchise.films.size} films"
                }

                UnifiedListItemCard(
                    title = franchise.nom,
                    subtitle = subtitle,
                    onClick = {
                        // Handle both schemas safely: prefer sub-sagas when present, otherwise direct films.
                        when {
                            franchise.sous_sagas.isNotEmpty() -> onFanchiseSousSagaClick(franchise.sous_sagas)
                            franchise.films.isNotEmpty() -> onFranchiseFilmsClick(franchise.films)
                        }
                    }
                )
            }
        }
    }
    /*Column(
        modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                var malonetunes = Category(categorie = "Malonetunes")
                var fraudchise = Franchise("123456789", "Commissar Donvhan")
                var firstSaga = SousSaga("ISEN, dawn of war")
                var secondSaga = SousSaga("Donvhan, rise of demon hunters")
                firstSaga.films = listOf<Film>(Film("8008142", 0, "Donvhan, the new Commissar", 2025, "Action Sci&Fi"), Film("78951239874563219876543211475369", 2, "ISEN's -2 floor, the last crusade", 2028, "Action Drama"))
                secondSaga.films = listOf<Film>(Film("456", 0, "Malo, the first rookie", 2025, genre = "Action"), Film("321", 1, "Anne-Amélie the last specialist", 2027, "Action Adventure"))
                fraudchise.sous_sagas = listOf<SousSaga>(firstSaga, secondSaga)
                malonetunes.franchises = listOf<Franchise>(fraudchise)
                ref.child("7").child("Malo_Cinematic_Universe").setValue(malonetunes)
                ref.child("7").child("Malo_Cinematic_Universe")
            }
        ) {
            Text("Ajouter Malo's Tomfooleryverse")
        }
    }*/
}



