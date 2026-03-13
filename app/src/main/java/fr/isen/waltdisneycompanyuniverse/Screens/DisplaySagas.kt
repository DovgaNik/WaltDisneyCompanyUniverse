package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.isen.waltdisneycompanyuniverse.datas.SousSaga

/*
This function is intended to display the saas of a franchise.
It should provide with a display consisting of:
 [TODO] Listing a set amount of sagas.
 [TODO] Displaying
 */
@Composable
fun DisplaySagas(modifier: Modifier = Modifier, saga: List<SousSaga>){
    LazyColumn(
        modifier = modifier
    ) { }
}