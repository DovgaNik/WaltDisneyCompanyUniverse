package fr.isen.waltdisneycompanyuniverse.datas
data class Category(
    var categorie: String = "",
    var franchises: List<Franchise> = emptyList<Franchise>()
)

data class Franchise(
    var id: String = "",
    var nom: String = "",
    var sous_sagas: List<SousSaga> = emptyList<SousSaga>(),
    var films: List<Film> = emptyList<Film>()
)

data class SousSaga(
    var nom: String = "",
    var films: List<Film> = emptyList<Film>()
)

data class Film(
    var id: String = "",
    var numero: Int = 0,
    var titre: String = "",
    var annee: Int = 0,
    var genre: String = ""
)