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

    val ref = FirebaseDatabase
        .getInstance()
        .getReference(sagas_and_films)

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }

    /*DisposableEffect(Unit) {

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FIREBASE", snapshot.value.toString())

                val list = mutableListOf<Category>()

                for (child in snapshot.children) {
                    val category = child.getValue(Category::class.java)
                    category?.let { list.add(it) }
                }
                Log.d("FIREBASE", snapshot.value.toString())

                categories = list
                Log.d("FIREBASE", snapshot.value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE", "Erreur", error.toException())
            }
        }

        ref.addValueEventListener(listener)

        onDispose {
            ref.removeEventListener(listener)
        }
    }*/

    DisplayListOfFaS(modifier, ref, categories)
}


@Composable
fun DisplayListOfFaS(modifier: Modifier = Modifier, ref: DatabaseReference, categories: List<Category>) {
    Log.d("DEBUG", "DisplayListOfFaS activé !")
    var page_number:Int = 0
    LazyColumn (
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        items(categories[page_number].franchises) { franchise ->
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    if (franchise.sous_sagas == emptyList<SousSaga>() && franchise.films != emptyList<Film>()){

                    }
                },
                colors = CardDefaults.cardColors(
                    containerColor = Color.Yellow,
                ),
            ) {
                Column() {
                    Text(text = franchise.nom, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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



