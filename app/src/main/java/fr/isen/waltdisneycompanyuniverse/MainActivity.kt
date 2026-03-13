package fr.isen.waltdisneycompanyuniverse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import fr.isen.waltdisneycompanyuniverse.datas.Film
import fr.isen.waltdisneycompanyuniverse.datas.pronounsList
import fr.isen.waltdisneycompanyuniverse.Screens.AuthScreen
import fr.isen.waltdisneycompanyuniverse.Screens.MainScreen
import fr.isen.waltdisneycompanyuniverse.Screens.NameOnboardingScreen
import fr.isen.waltdisneycompanyuniverse.Screens.ProfilePictureOnboardingScreen
import fr.isen.waltdisneycompanyuniverse.Screens.PronounsOnboardingScreen
import fr.isen.waltdisneycompanyuniverse.Screens.saveUserToFirebase
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
    Auth, OnboardingName, OnboardingPronouns, OnboardingProfilePicture, Welcome, Home
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaltDisneyCompanyUniverseTheme(darkTheme = true) {
                var currentScreen by remember { mutableStateOf(AppScreen.Auth) }
                var userName by remember { mutableStateOf("") }
                var userPronounsIndex by remember { mutableIntStateOf(-1) }
                var selectedPfpIndex by remember { mutableIntStateOf(-1) }
                var isFirstTime by remember { mutableStateOf(false) }
                var isLoading by remember { mutableStateOf(false) }
                var requestedFilmUuid by remember { mutableStateOf("9dd2ab1d-d32e-44ed-9e43-ea97e4697cb9") }
                var selectedFilm by remember { mutableStateOf<Film?>(null) }
                var isFilmLoading by remember { mutableStateOf(false) }
                var filmLoadError by remember { mutableStateOf<String?>(null) }
                var posterUrl by remember { mutableStateOf<String?>(null) }
                var isPosterLoading by remember { mutableStateOf(false) }
                var posterLoadError by remember { mutableStateOf<String?>(null) }
                var posterRetryToken by remember { mutableIntStateOf(0) }

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
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
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
                                currentScreen = AppScreen.Welcome
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            if (isLoading) {
                                isLoading = false
                                currentScreen = AppScreen.Welcome
                            }
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
                            isFilmLoading = false
                        }
                    })
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
                                            AppScreen.Home -> MainScreen(
                                                userName = userName,
                                                pfpResId = if (selectedPfpIndex != -1) profilePictures[selectedPfpIndex] else R.drawable.pfp_mickey,
                                                film = selectedFilm,
                                                filmUuid = requestedFilmUuid,
                                                isFilmLoading = isFilmLoading,
                                                filmError = filmLoadError,
                                                posterUrl = posterUrl,
                                                isPosterLoading = isPosterLoading,
                                                posterError = posterLoadError,
                                                onRetryFilmLoad = { fetchFilmByUuid(requestedFilmUuid) },
                                                onRetryPosterLoad = { posterRetryToken++ },
                                                onProfileClick = {
                                                    val intent = Intent(this@MainActivity, EditProfileActivity::class.java)
                                                    startActivity(intent)
                                                }
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
