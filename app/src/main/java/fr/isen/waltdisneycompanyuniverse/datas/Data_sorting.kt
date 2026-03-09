package fr.isen.waltdisneycompanyuniverse.datas
data class Category(
    val categorie: String = "",
    val franchises: List<Franchise> = emptyList()
)

data class Franchise(
    val id: String = "",
    val nom: String = "",
    val sous_sagas: List<SousSaga> = emptyList()
)

data class SousSaga(
    val nom: String = "",
    val films: List<Film> = emptyList()
)

data class Film(
    val id: String = "",
    val numero: Int = 0,
    val titre: String = "",
    val annee: Int = 0,
    val genre: String = ""
)