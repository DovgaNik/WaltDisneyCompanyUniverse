package fr.isen.waltdisneycompanyuniverse.Screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
        .getReference(sagas_and_films)

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
    var selectedFranchise by remember { mutableStateOf<Franchise?>(null) }

    if (selectedFranchise == null){
        DisplayFranchises(
            modifier,
            ref,
            categories
        ) { franchise ->
            selectedFranchise = franchise
        }
    } else {
        DisplayFilms(
            modifier,
            null,
            selectedFranchise!!.films
        )
    }
}


@Composable
fun DisplayFranchises(modifier: Modifier = Modifier, ref: DatabaseReference, categories: List<Category>, onFranchiseClick: (Franchise) -> Unit) {
    Log.d("DEBUG", "DisplayListOfFaS activé !")
    LazyColumn (
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        categories.forEach { categorie ->
            items(categorie.franchises) { franchise ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        // On vérifie si c'est une franchise qui fonctionne en sagas ou juste en films.
                        if (franchise.sous_sagas == emptyList<SousSaga>() && franchise.films != emptyList<Film>()){
                            onFranchiseClick(franchise)
                        }
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Yellow,
                    ),
                ) {
                    Column {
                        Text(text = franchise.nom, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        /*categories.forEach { category ->
            item { Text(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                text = category.categorie,
            ) }
            category.franchises.forEach { franchise ->
                item { Text(franchise.nom) }
                franchise.sous_sagas.forEach { saga ->
                    saga.films.forEach { film ->
                        item { Text("${film.numero} - ${film.titre}") }
                    }
                }
            }
        }*/
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



