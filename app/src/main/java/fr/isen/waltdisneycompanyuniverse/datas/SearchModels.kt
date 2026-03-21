package fr.isen.waltdisneycompanyuniverse.datas

enum class SearchParameter(val label: String) {
    All("All"),
    Film("Films"),
    Franchise("Franchises"),
    Saga("Sagas"),
    Category("Categories")
}

data class SearchIndexEntry(
    val filmId: String,
    val filmTitle: String,
    val year: Int,
    val genre: String,
    val franchise: String,
    val saga: String,
    val category: String
)

