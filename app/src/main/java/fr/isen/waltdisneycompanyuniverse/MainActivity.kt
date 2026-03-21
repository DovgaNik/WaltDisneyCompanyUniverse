package fr.isen.waltdisneycompanyuniverse

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.waltdisneycompanyuniverse.Screens.AuthScreen
import fr.isen.waltdisneycompanyuniverse.Screens.AppBottomNavBar
import fr.isen.waltdisneycompanyuniverse.Screens.MainScreen
import fr.isen.waltdisneycompanyuniverse.Screens.MarkedMoviesScreen
import fr.isen.waltdisneycompanyuniverse.Screens.NameOnboardingScreen
import fr.isen.waltdisneycompanyuniverse.Screens.PersistentTopHeader
import fr.isen.waltdisneycompanyuniverse.Screens.Prologue
import fr.isen.waltdisneycompanyuniverse.Screens.ProfilePictureOnboardingScreen
import fr.isen.waltdisneycompanyuniverse.Screens.PronounsOnboardingScreen
import fr.isen.waltdisneycompanyuniverse.Screens.SearchScreen
import fr.isen.waltdisneycompanyuniverse.Screens.saveUserToFirebase
import fr.isen.waltdisneycompanyuniverse.datas.Film
import fr.isen.waltdisneycompanyuniverse.datas.SearchIndexEntry
import fr.isen.waltdisneycompanyuniverse.datas.collectionNode
import fr.isen.waltdisneycompanyuniverse.datas.collectionStatusKeys
import fr.isen.waltdisneycompanyuniverse.datas.markedMoviesStatusOrder
import fr.isen.waltdisneycompanyuniverse.datas.pronounsList
import fr.isen.waltdisneycompanyuniverse.datas.statusWantToGetRid
import fr.isen.waltdisneycompanyuniverse.datas.statusWatched
import fr.isen.waltdisneycompanyuniverse.datas.statusWantToWatch
import fr.isen.waltdisneycompanyuniverse.datas.usersNode
import fr.isen.waltdisneycompanyuniverse.ui.theme.DisneyBlue
import fr.isen.waltdisneycompanyuniverse.ui.theme.DisneyDeepBlue
import fr.isen.waltdisneycompanyuniverse.ui.theme.WaltDisneyCompanyUniverseTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

enum class AppScreen {
    Auth, OnboardingName, OnboardingPronouns, OnboardingProfilePicture, Welcome, Home, Categories, MarkedMovies, Search
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaltDisneyCompanyUniverseTheme(darkTheme = true) {
                var currentScreen by remember { mutableStateOf(AppScreen.Auth) }
                var previousScreen by remember { mutableStateOf<AppScreen?>(null) }
                var userName by remember { mutableStateOf("") }
                var userPronounsIndex by remember { mutableIntStateOf(-1) }
                var selectedPfpIndex by remember { mutableIntStateOf(-1) }
                var isFirstTime by remember { mutableStateOf(false) }
                var isLoading by remember { mutableStateOf(true) }
                var requestedFilmUuid by remember { mutableStateOf("732725b2-beb5-4e04-8909-81e5ed12dbdc") }
                var selectedFilm by remember { mutableStateOf<Film?>(null) }
                var isFilmLoading by remember { mutableStateOf(false) }
                var filmLoadError by remember { mutableStateOf<String?>(null) }
                var posterUrl by remember { mutableStateOf<String?>(null) }
                var isPosterLoading by remember { mutableStateOf(false) }
                var posterLoadError by remember { mutableStateOf<String?>(null) }
                var posterRetryToken by remember { mutableIntStateOf(0) }
                var trailerUrl by remember { mutableStateOf<String?>(null) }
                var isTrailerLoading by remember { mutableStateOf(false) }
                var trailerLoadError by remember { mutableStateOf<String?>(null) }
                var trailerRetryToken by remember { mutableIntStateOf(0) }
                var userFilmStatuses by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
                var usersWantingToGetRid by remember { mutableStateOf<List<String>>(emptyList()) }
                var isLoadingUsersWantingToGetRid by remember { mutableStateOf(false) }
                var allFilmsById by remember { mutableStateOf<Map<String, Film>>(emptyMap()) }
                var searchIndexEntries by remember { mutableStateOf<List<SearchIndexEntry>>(emptyList()) }

                val profilePictures = listOf(
                    R.drawable.pfp_mickey,
                    R.drawable.pfp_donald,
                    R.drawable.pfp_elsa,
                    R.drawable.pfp_cruella,
                    R.drawable.pfp_thor,
                    R.drawable.pfp_cats,
                    R.drawable.pfp_darkvador,
                    R.drawable.pfp_captain_jack_sparrow,
                    R.drawable.pfp_spiderman,
                    R.drawable.pfp_chipmunks,
                    R.drawable.pfp_belle,
                    R.drawable.pfp_hulk
                )

                fun startListeningToUserData() {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid == null) {
                        isLoading = false
                        currentScreen = AppScreen.Auth
                        userFilmStatuses = emptyMap()
                        return
                    }
                    isLoading = true
                    val userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("persona")
                    
                    userRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                userName = snapshot.child("username").getValue(String::class.java) ?: ""
                                selectedPfpIndex = snapshot.child("pfp").getValue(Int::class.java) ?: -1
                                userPronounsIndex = snapshot.child("pronouns").getValue(Int::class.java) ?: -1
                            }
                            if (isLoading) {
                                isLoading = false
                                currentScreen = if (snapshot.exists()) AppScreen.Welcome else AppScreen.OnboardingName
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            if (isLoading) {
                                isLoading = false
                                currentScreen = AppScreen.Welcome
                            }
                        }
                    })

                    FirebaseDatabase.getInstance().reference
                        .child(usersNode)
                        .child(uid)
                        .child(collectionNode)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val byFilmId = mutableMapOf<String, MutableList<String>>()
                                collectionStatusKeys.forEach { statusKey ->
                                    snapshot.child(statusKey).children.forEach { filmSnapshot ->
                                        val filmId = filmSnapshot.key?.trim().orEmpty()
                                        val isMarked = filmSnapshot.getValue(Boolean::class.java) == true
                                        if (filmId.isNotBlank() && isMarked) {
                                            byFilmId.getOrPut(filmId) { mutableListOf() }.add(statusKey)
                                        }
                                    }
                                }
                                userFilmStatuses = byFilmId
                            }

                            override fun onCancelled(error: DatabaseError) {
                                userFilmStatuses = emptyMap()
                            }
                        })
                }

                fun updateFilmCollectionStatus(filmId: String, statusKey: String) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
                    val normalizedFilmId = filmId.trim()
                    if (normalizedFilmId.isBlank()) return

                    val userCollectionRef = FirebaseDatabase.getInstance().reference
                        .child(usersNode)
                        .child(uid)
                        .child(collectionNode)

                    val currentStatuses = userFilmStatuses[normalizedFilmId] ?: emptyList()
                    val wasSelected = currentStatuses.contains(statusKey)
                    val updates = mutableMapOf<String, Any?>()

                    if (wasSelected) {
                        updates["$statusKey/$normalizedFilmId"] = null
                    } else {
                        // Mutually exclusive: Watched vs Want to watch
                        if (statusKey == statusWatched) {
                            updates["$statusWantToWatch/$normalizedFilmId"] = null
                        } else if (statusKey == statusWantToWatch) {
                            updates["$statusWatched/$normalizedFilmId"] = null
                        }
                        
                        updates["$statusKey/$normalizedFilmId"] = true
                    }

                    userCollectionRef.updateChildren(updates)
                }

                fun fetchUsersWantingToGetRid(filmId: String?) {
                    val normalizedFilmId = filmId?.trim().orEmpty()
                    if (normalizedFilmId.isBlank()) {
                        usersWantingToGetRid = emptyList()
                        isLoadingUsersWantingToGetRid = false
                        return
                    }

                    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
                    isLoadingUsersWantingToGetRid = true
                    FirebaseDatabase.getInstance().reference
                        .child(usersNode)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val names = mutableListOf<String>()
                                snapshot.children.forEach { userSnapshot ->
                                    if (userSnapshot.key == currentUid) return@forEach
                                    val hasStatus = userSnapshot
                                        .child(collectionNode)
                                        .child(statusWantToGetRid)
                                        .child(normalizedFilmId)
                                        .getValue(Boolean::class.java) == true
                                    if (!hasStatus) return@forEach

                                    val username = userSnapshot
                                        .child("persona")
                                        .child("username")
                                        .getValue(String::class.java)
                                        ?.trim()
                                        .orEmpty()
                                    if (username.isNotBlank()) {
                                        names.add(username)
                                    }
                                }
                                usersWantingToGetRid = names.distinct().sorted()
                                isLoadingUsersWantingToGetRid = false
                            }

                            override fun onCancelled(error: DatabaseError) {
                                usersWantingToGetRid = emptyList()
                                isLoadingUsersWantingToGetRid = false
                            }
                        })
                }

                fun startListeningToFilmCatalog() {
                    FirebaseDatabase.getInstance().reference
                        .child("categories")
                        .addValueEventListener(object : ValueEventListener {
                            fun isFilmNode(snapshot: DataSnapshot): Boolean {
                                return snapshot.hasChild("titre") && snapshot.hasChild("annee") && snapshot.hasChild("genre")
                            }

                            fun toFilm(snapshot: DataSnapshot): Film {
                                val rawId = snapshot.child("id").getValue(String::class.java)?.trim().orEmpty()
                                val fallbackId = snapshot.key?.trim().orEmpty()
                                return Film(
                                    id = if (rawId.isNotBlank()) rawId else fallbackId,
                                    numero = snapshot.child("numero").getValue(Int::class.java) ?: 0,
                                    titre = snapshot.child("titre").getValue(String::class.java).orEmpty(),
                                    annee = snapshot.child("annee").getValue(Int::class.java) ?: 0,
                                    genre = snapshot.child("genre").getValue(String::class.java).orEmpty()
                                )
                            }

                            fun collectFilmsRecursively(snapshot: DataSnapshot, collected: MutableMap<String, Film>) {
                                if (isFilmNode(snapshot)) {
                                    val film = toFilm(snapshot)
                                    if (film.id.isNotBlank()) {
                                        collected[film.id] = film
                                    }
                                }
                                snapshot.children.forEach { child ->
                                    collectFilmsRecursively(child, collected)
                                }
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                val collected = mutableMapOf<String, Film>()
                                val searchEntries = mutableMapOf<String, SearchIndexEntry>()

                                fun putFilmEntry(
                                    film: Film,
                                    category: String,
                                    franchise: String,
                                    saga: String
                                ) {
                                    if (film.id.isBlank()) return
                                    collected[film.id] = film
                                    searchEntries[film.id] = SearchIndexEntry(
                                        filmId = film.id,
                                        filmTitle = film.titre,
                                        year = film.annee,
                                        genre = film.genre,
                                        franchise = franchise,
                                        saga = saga,
                                        category = category
                                    )
                                }

                                snapshot.children.forEach { categorySnapshot ->
                                    val categoryName = categorySnapshot.child("categorie").getValue(String::class.java).orEmpty()
                                    categorySnapshot.child("franchises").children.forEach { franchiseSnapshot ->
                                        val franchiseName = franchiseSnapshot.child("nom").getValue(String::class.java).orEmpty()

                                        franchiseSnapshot.child("films").children.forEach { filmSnapshot ->
                                            val film = toFilm(filmSnapshot)
                                            putFilmEntry(film, categoryName, franchiseName, "")
                                        }

                                        franchiseSnapshot.child("sous_sagas").children.forEach { sagaSnapshot ->
                                            val sagaName = sagaSnapshot.child("nom").getValue(String::class.java).orEmpty()
                                            sagaSnapshot.child("films").children.forEach { filmSnapshot ->
                                                val film = toFilm(filmSnapshot)
                                                putFilmEntry(film, categoryName, franchiseName, sagaName)
                                            }
                                        }
                                    }
                                }

                                if (collected.isEmpty()) {
                                    collectFilmsRecursively(snapshot, collected)
                                }

                                allFilmsById = collected
                                searchIndexEntries = searchEntries.values.sortedBy { it.filmTitle }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                allFilmsById = emptyMap()
                                searchIndexEntries = emptyList()
                            }
                        })
                }

                suspend fun fetchPosterUrlFromTmdb(title: String): String? {
                    val apiKey = BuildConfig.TMDB_API_KEY.trim()
                    if (apiKey.isBlank()) {
                        throw IllegalStateException("TMDB_API_KEY is missing in local.properties")
                    }

                    val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
                    val endpoint = "https://api.themoviedb.org/3/search/movie?query=$encodedTitle&api_key=$apiKey"

                    return withContext(Dispatchers.IO) {
                        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                            requestMethod = "GET"
                            connectTimeout = 10_000
                            readTimeout = 10_000
                        }

                        try {
                            val responseCode = connection.responseCode
                            val body = if (responseCode in 200..299) {
                                connection.inputStream.bufferedReader().use { it.readText() }
                            } else {
                                connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                            }

                            if (responseCode !in 200..299) {
                                throw IOException("TMDB request failed ($responseCode): $body")
                            }

                            val results = JSONObject(body).optJSONArray("results") ?: return@withContext null
                            for (index in 0 until results.length()) {
                                val result = results.optJSONObject(index) ?: continue
                                val posterPath = result.optString("poster_path")
                                if (!posterPath.isNullOrBlank() && posterPath != "null") {
                                    return@withContext "https://image.tmdb.org/t/p/w500$posterPath"
                                }
                            }
                            null
                        } finally {
                            connection.disconnect()
                        }
                    }
                }

                suspend fun fetchTrailerUrlFromTmdb(title: String): String? {
                    val apiKey = BuildConfig.TMDB_API_KEY.trim()
                    if (apiKey.isBlank()) {
                        throw IllegalStateException("TMDB_API_KEY is missing in local.properties")
                    }

                    val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
                    val searchEndpoint = "https://api.themoviedb.org/3/search/movie?query=$encodedTitle&api_key=$apiKey"

                    return withContext(Dispatchers.IO) {
                        fun readBody(connection: HttpURLConnection, successful: Boolean): String {
                            return if (successful) {
                                connection.inputStream.bufferedReader().use { it.readText() }
                            } else {
                                connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                            }
                        }

                        val searchConnection = (URL(searchEndpoint).openConnection() as HttpURLConnection).apply {
                            requestMethod = "GET"
                            connectTimeout = 10_000
                            readTimeout = 10_000
                        }

                        val tmdbMovieId = try {
                            val responseCode = searchConnection.responseCode
                            val body = readBody(searchConnection, responseCode in 200..299)
                            if (responseCode !in 200..299) {
                                throw IOException("TMDB search failed ($responseCode): $body")
                            }

                            val results = JSONObject(body).optJSONArray("results") ?: return@withContext null
                            if (results.length() == 0) return@withContext null
                            val firstMovie = results.optJSONObject(0) ?: return@withContext null
                            if (!firstMovie.has("id")) return@withContext null
                            firstMovie.optInt("id")
                        } finally {
                            searchConnection.disconnect()
                        }

                        val videosEndpoint = "https://api.themoviedb.org/3/movie/$tmdbMovieId/videos?api_key=$apiKey"
                        val videosConnection = (URL(videosEndpoint).openConnection() as HttpURLConnection).apply {
                            requestMethod = "GET"
                            connectTimeout = 10_000
                            readTimeout = 10_000
                        }

                        try {
                            val responseCode = videosConnection.responseCode
                            val body = readBody(videosConnection, responseCode in 200..299)
                            if (responseCode !in 200..299) {
                                throw IOException("TMDB videos failed ($responseCode): $body")
                            }

                            val results = JSONObject(body).optJSONArray("results") ?: return@withContext null

                            var youtubeOfficialTrailer: String? = null
                            var youtubeTrailer: String? = null
                            var youtubeAny: String? = null

                            for (index in 0 until results.length()) {
                                val video = results.optJSONObject(index) ?: continue
                                val key = video.optString("key")
                                val site = video.optString("site")
                                val type = video.optString("type")
                                val official = video.optBoolean("official", false)
                                if (key.isBlank() || site != "YouTube") continue

                                val youtubeUrl = "https://www.youtube.com/watch?v=$key"
                                if (official && type == "Trailer" && youtubeOfficialTrailer == null) {
                                    youtubeOfficialTrailer = youtubeUrl
                                }
                                if (type == "Trailer" && youtubeTrailer == null) {
                                    youtubeTrailer = youtubeUrl
                                }
                                if (youtubeAny == null) {
                                    youtubeAny = youtubeUrl
                                }
                            }

                            youtubeOfficialTrailer ?: youtubeTrailer ?: youtubeAny
                        } finally {
                            videosConnection.disconnect()
                        }
                    }
                }

                fun fetchFilmByUuid(uuid: String) {
                    val normalizedUuid = uuid.trim()
                    if (normalizedUuid.isBlank()) {
                        selectedFilm = null
                        filmLoadError = "Missing film UUID"
                        isFilmLoading = false
                        return
                    }

                    isFilmLoading = true
                    filmLoadError = null
                    selectedFilm = null
                    posterUrl = null
                    isPosterLoading = false
                    posterLoadError = null
                    trailerUrl = null
                    isTrailerLoading = false
                    trailerLoadError = null

                    val categoriesRef = FirebaseDatabase.getInstance().reference.child("categories")
                    categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        fun idMatches(snapshot: DataSnapshot, target: String): Boolean {
                            val rawId = snapshot.child("id").getValue(String::class.java)?.trim()
                            val fallbackKey = snapshot.key?.trim()
                            return rawId.equals(target, ignoreCase = true) || fallbackKey.equals(target, ignoreCase = true)
                        }

                        fun isFilmNode(snapshot: DataSnapshot): Boolean {
                            return snapshot.hasChild("titre") && snapshot.hasChild("annee") && snapshot.hasChild("genre")
                        }

                        fun toFilm(snapshot: DataSnapshot): Film {
                            val rawId = snapshot.child("id").getValue(String::class.java)?.trim().orEmpty()
                            val fallbackId = snapshot.key?.trim().orEmpty()
                            return Film(
                                id = if (rawId.isNotBlank()) rawId else fallbackId,
                                numero = snapshot.child("numero").getValue(Int::class.java) ?: 0,
                                titre = snapshot.child("titre").getValue(String::class.java).orEmpty(),
                                annee = snapshot.child("annee").getValue(Int::class.java) ?: 0,
                                genre = snapshot.child("genre").getValue(String::class.java).orEmpty()
                            )
                        }

                        fun findFilmRecursively(snapshot: DataSnapshot, target: String): Film? {
                            if (idMatches(snapshot, target) && isFilmNode(snapshot)) {
                                return toFilm(snapshot)
                            }
                            for (child in snapshot.children) {
                                val candidate = findFilmRecursively(child, target)
                                if (candidate != null) return candidate
                            }
                            return null
                        }

                        fun findAnyIdRecursively(snapshot: DataSnapshot, target: String): Boolean {
                            if (idMatches(snapshot, target)) {
                                return true
                            }
                            for (child in snapshot.children) {
                                if (findAnyIdRecursively(child, target)) return true
                            }
                            return false
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val foundFilm = findFilmRecursively(snapshot, normalizedUuid)
                            val idExistsButNotFilm = foundFilm == null && findAnyIdRecursively(snapshot, normalizedUuid)

                            selectedFilm = foundFilm
                            filmLoadError = if (idExistsButNotFilm) {
                                "UUID exists but it is not a film id"
                            } else {
                                null
                            }
                            isFilmLoading = false
                        }

                        override fun onCancelled(error: DatabaseError) {
                            filmLoadError = error.message
                            selectedFilm = null
                            posterUrl = null
                            posterLoadError = null
                            isPosterLoading = false
                            trailerUrl = null
                            trailerLoadError = null
                            isTrailerLoading = false
                            isFilmLoading = false
                        }
                    })
                }

                fun fetchRandomFilmUuid(onResult: (String?) -> Unit) {
                    val categoriesRef = FirebaseDatabase.getInstance().reference.child("categories")
                    categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        fun collectFilmIds(snapshot: DataSnapshot, collected: MutableList<String>) {
                            val hasFilmShape = snapshot.hasChild("titre") && snapshot.hasChild("annee") && snapshot.hasChild("genre")
                            if (hasFilmShape) {
                                val rawId = snapshot.child("id").getValue(String::class.java)?.trim().orEmpty()
                                val fallbackId = snapshot.key?.trim().orEmpty()
                                val resolvedId = if (rawId.isNotBlank()) rawId else fallbackId
                                if (resolvedId.isNotBlank()) {
                                    collected.add(resolvedId)
                                }
                            }
                            snapshot.children.forEach { child ->
                                collectFilmIds(child, collected)
                            }
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val ids = mutableListOf<String>()
                            collectFilmIds(snapshot, ids)
                            onResult(ids.randomOrNull())
                        }

                        override fun onCancelled(error: DatabaseError) {
                            onResult(null)
                        }
                    })
                }

                LaunchedEffect(Unit) {
                    startListeningToFilmCatalog()

                    fetchRandomFilmUuid { randomUuid ->
                        if (!randomUuid.isNullOrBlank()) {
                            requestedFilmUuid = randomUuid
                        }
                    }

                    val existingUser = FirebaseAuth.getInstance().currentUser
                    if (existingUser == null) {
                        isLoading = false
                        currentScreen = AppScreen.Auth
                        userFilmStatuses = emptyMap()
                        usersWantingToGetRid = emptyList()
                    } else {
                        isFirstTime = false
                        startListeningToUserData()
                    }
                }

                LaunchedEffect(currentScreen, requestedFilmUuid) {
                    if (currentScreen == AppScreen.Home) {
                        fetchFilmByUuid(requestedFilmUuid)
                    }
                }

                LaunchedEffect(selectedFilm?.id, selectedFilm?.titre, posterRetryToken) {
                    val title = selectedFilm?.titre?.trim().orEmpty()
                    if (title.isBlank()) {
                        posterUrl = null
                        posterLoadError = null
                        isPosterLoading = false
                        return@LaunchedEffect
                    }

                    isPosterLoading = true
                    posterLoadError = null
                    posterUrl = null

                    try {
                        posterUrl = fetchPosterUrlFromTmdb(title)
                        if (posterUrl == null) {
                            posterLoadError = "No poster found on TMDB"
                        }
                    } catch (exception: Exception) {
                        posterLoadError = exception.message ?: "Failed to load poster"
                    } finally {
                        isPosterLoading = false
                    }
                }

                LaunchedEffect(selectedFilm?.id, selectedFilm?.titre, trailerRetryToken) {
                    val title = selectedFilm?.titre?.trim().orEmpty()
                    if (title.isBlank()) {
                        trailerUrl = null
                        trailerLoadError = null
                        isTrailerLoading = false
                        return@LaunchedEffect
                    }

                    isTrailerLoading = true
                    trailerLoadError = null
                    trailerUrl = null

                    try {
                        trailerUrl = fetchTrailerUrlFromTmdb(title)
                        if (trailerUrl == null) {
                            trailerLoadError = "No trailer found on TMDB"
                        }
                    } catch (exception: Exception) {
                        trailerLoadError = exception.message ?: "Failed to load trailer"
                    } finally {
                        isTrailerLoading = false
                    }
                }

                LaunchedEffect(selectedFilm?.id) {
                    fetchUsersWantingToGetRid(selectedFilm?.id)
                }

                val markedSections = markedMoviesStatusOrder.mapNotNull { statusKey ->
                    val filmsForStatus = userFilmStatuses
                        .asSequence()
                        .filter { (_, selectedStatuses) -> selectedStatuses.contains(statusKey) }
                        .mapNotNull { (filmId, _) -> allFilmsById[filmId] }
                        .sortedBy { film -> film.numero }
                        .toList()

                    if (filmsForStatus.isNotEmpty()) statusKey to filmsForStatus else null
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0.0f to DisneyDeepBlue,
                                    0.48f to DisneyBlue,
                                    1.0f to DisneyBlue
                                )
                            )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            )
                        } else {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                containerColor = Color.Transparent
                            ) { innerPadding ->
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    Crossfade(
                                        targetState = currentScreen,
                                        animationSpec = tween(durationMillis = 1000),
                                        label = "ScreenTransition"
                                    ) { screen ->
                                        when (screen) {
                                            AppScreen.Auth -> AuthScreen(
                                                onAuthSuccess = { isSignUp ->
                                                    isFirstTime = isSignUp
                                                    if (isSignUp) {
                                                        currentScreen = AppScreen.OnboardingName
                                                    } else {
                                                        startListeningToUserData()
                                                    }
                                                }
                                            )
                                            AppScreen.OnboardingName -> NameOnboardingScreen(
                                                onNameSubmitted = { name ->
                                                    userName = name
                                                    currentScreen = AppScreen.OnboardingPronouns
                                                }
                                            )
                                            AppScreen.OnboardingPronouns -> PronounsOnboardingScreen(
                                                onPronounsSelected = { pronouns ->
                                                    userPronounsIndex = pronounsList.indexOf(pronouns)
                                                    currentScreen = AppScreen.OnboardingProfilePicture
                                                }
                                            )
                                            AppScreen.OnboardingProfilePicture -> ProfilePictureOnboardingScreen(
                                                onFinish = { pictureIndex ->
                                                    if (pictureIndex != null) {
                                                        selectedPfpIndex = pictureIndex
                                                    }
                                                    // Save to Firebase Database
                                                    saveUserToFirebase(
                                                        username = userName,
                                                        pronouns = userPronounsIndex,
                                                        pfp = selectedPfpIndex.takeIf { it != -1 } ?: 0
                                                    )
                                                    // Start listening for changes after onboarding
                                                    startListeningToUserData()
                                                }
                                            )
                                            AppScreen.Welcome -> WelcomeScreen(
                                                name = userName.ifEmpty { "User" },
                                                pfpResId = if (selectedPfpIndex != -1) profilePictures[selectedPfpIndex] else R.drawable.pfp_mickey,
                                                isFirstTime = isFirstTime,
                                                onTimeout = { currentScreen = AppScreen.Home }
                                            )
                                            AppScreen.Home,
                                            AppScreen.Categories,
                                            AppScreen.MarkedMovies,
                                            AppScreen.Search -> Column(modifier = Modifier.fillMaxSize()) {
                                                val currentPfpRes = if (selectedPfpIndex != -1) {
                                                    profilePictures[selectedPfpIndex]
                                                } else {
                                                    R.drawable.pfp_mickey
                                                }

                                                PersistentTopHeader(
                                                    userName = userName.ifEmpty { "Username" },
                                                    pfpResId = currentPfpRes,
                                                    onProfileClick = {
                                                        val intent = Intent(this@MainActivity, EditProfileActivity::class.java)
                                                        startActivity(intent)
                                                    }
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth()
                                                ) {
                                                    when (screen) {
                                                        AppScreen.Home -> MainScreen(
                                                            film = selectedFilm,
                                                            filmUuid = requestedFilmUuid,
                                                            isFilmLoading = isFilmLoading,
                                                            filmError = filmLoadError,
                                                            posterUrl = posterUrl,
                                                            isPosterLoading = isPosterLoading,
                                                            posterError = posterLoadError,
                                                            trailerUrl = trailerUrl,
                                                            isTrailerLoading = isTrailerLoading,
                                                            trailerError = trailerLoadError,
                                                            currentFilmStatuses = selectedFilm?.id?.let { userFilmStatuses[it] } ?: emptyList(),
                                                            onCollectionStatusSelected = { statusKey ->
                                                                selectedFilm?.id?.let { updateFilmCollectionStatus(it, statusKey) }
                                                            },
                                                            usersWantingToGetRid = usersWantingToGetRid,
                                                            isLoadingUsersWantingToGetRid = isLoadingUsersWantingToGetRid,
                                                            onRetryFilmLoad = { fetchFilmByUuid(requestedFilmUuid) },
                                                            onRetryPosterLoad = { posterRetryToken++ },
                                                            onRetryTrailerLoad = { trailerRetryToken++ },
                                                            onOpenTrailer = { url ->
                                                                val trailerIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                                startActivity(trailerIntent)
                                                            },
                                                            onBack = previousScreen?.let { prev ->
                                                                {
                                                                    currentScreen = prev
                                                                    previousScreen = null
                                                                }
                                                            }
                                                        )

                                                        AppScreen.Categories -> Prologue(
                                                            modifier = Modifier.fillMaxSize(),
                                                            filmStatuses = userFilmStatuses,
                                                            onFilmSelected = { film ->
                                                                val filmId = film.id.trim()
                                                                if (filmId.isNotBlank()) {
                                                                    requestedFilmUuid = filmId
                                                                    previousScreen = AppScreen.Categories
                                                                    currentScreen = AppScreen.Home
                                                                }
                                                            }
                                                        )

                                                        AppScreen.MarkedMovies -> MarkedMoviesScreen(
                                                            modifier = Modifier.fillMaxSize(),
                                                            markedSections = markedSections,
                                                            onFilmSelected = { film ->
                                                                val filmId = film.id.trim()
                                                                if (filmId.isNotBlank()) {
                                                                    requestedFilmUuid = filmId
                                                                    previousScreen = AppScreen.MarkedMovies
                                                                    currentScreen = AppScreen.Home
                                                                }
                                                            }
                                                        )

                                                        AppScreen.Search -> SearchScreen(
                                                            modifier = Modifier.fillMaxSize(),
                                                            entries = searchIndexEntries,
                                                            onFilmSelected = { filmId ->
                                                                if (filmId.isNotBlank()) {
                                                                    requestedFilmUuid = filmId
                                                                    previousScreen = AppScreen.Search
                                                                    currentScreen = AppScreen.Home
                                                                }
                                                            }
                                                        )
                                                        else -> Unit
                                                    }
                                                }

                                                AppBottomNavBar(
                                                    currentScreen = screen,
                                                    onHomeClick = { 
                                                        previousScreen = null
                                                        currentScreen = AppScreen.Home 
                                                    },
                                                    onCategoriesClick = { currentScreen = AppScreen.Categories },
                                                    onFavoritesClick = { currentScreen = AppScreen.MarkedMovies },
                                                    onSearchClick = { currentScreen = AppScreen.Search }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(name: String, pfpResId: Int, isFirstTime: Boolean, onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2500) // Display welcome screen for 2.5 seconds
        onTimeout()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isFirstTime) "Welcome," else "Welcome back,",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Image(
            painter = painterResource(id = pfpResId),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}
