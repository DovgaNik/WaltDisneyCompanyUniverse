package fr.isen.waltdisneycompanyuniverse.Screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import fr.isen.waltdisneycompanyuniverse.datas.*

@Composable
fun Prologue(modifier: Modifier = Modifier) {

    val ref = FirebaseDatabase
        .getInstance()
        .getReference("categories")

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }

    DisposableEffect(Unit) {

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
    }

    DisplayListOfFaS(modifier, categories)
}


@Composable
fun DisplayListOfFaS(modifier: Modifier = Modifier, categories: List<Category>) {
    Log.d("DEBUG", "DisplayListOfFaS activé !")

    LazyColumn {
        categories.forEach { category ->

            item { Text(category.categorie) }

            category.franchises.forEach { franchise ->

                item { Text(franchise.nom) }

                franchise.sous_sagas.forEach { saga ->

                    saga.films.forEach { film ->
                        item { Text("${film.numero} - ${film.titre}") }
                    }

                }
            }
        }
    }
}