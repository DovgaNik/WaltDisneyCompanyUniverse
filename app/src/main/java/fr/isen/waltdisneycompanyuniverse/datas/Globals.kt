package fr.isen.waltdisneycompanyuniverse.datas

val categories: String = "categories"

const val usersNode: String = "users"
const val collectionNode: String = "collection"

const val statusWantToWatch: String = "want_to_watch"
const val statusWatched: String = "watched"
const val statusOwnDvdBluray: String = "own_dvd_bluray"
const val statusWantToGetRid: String = "want_to_get_rid"

val collectionStatusKeys = listOf(
	statusWantToWatch,
	statusWatched,
	statusOwnDvdBluray,
	statusWantToGetRid
)

fun statusLabel(statusKey: String): String = when (statusKey) {
	statusWantToWatch -> "Want to watch"
	statusWatched -> "Watched"
	statusOwnDvdBluray -> "Own on DVD/Blu-ray"
	statusWantToGetRid -> "Want to get rid"
	else -> ""
}

val pronounsList = listOf("He/Him", "She/Her", "They/Them", "Prefer not to say", "Other")