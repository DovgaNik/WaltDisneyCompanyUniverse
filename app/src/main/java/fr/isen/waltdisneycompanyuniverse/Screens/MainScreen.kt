package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.isen.waltdisneycompanyuniverse.AppScreen
import fr.isen.waltdisneycompanyuniverse.R
import fr.isen.waltdisneycompanyuniverse.datas.Film

@Composable
fun MainScreen(
    userName: String,
    pfpResId: Int,
    film: Film?,
    filmUuid: String,
    isFilmLoading: Boolean,
    filmError: String?,
    posterUrl: String?,
    isPosterLoading: Boolean,
    posterError: String?,
    trailerUrl: String?,
    isTrailerLoading: Boolean,
    trailerError: String?,
    onRetryFilmLoad: () -> Unit = {},
    onRetryPosterLoad: () -> Unit = {},
    onRetryTrailerLoad: () -> Unit = {},
    onOpenTrailer: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val isFilmNotFound = !isFilmLoading && filmError == null && film == null
    val displayedTitle = film?.titre?.takeIf { it.isNotBlank() } ?: "Unknown movie"
    val displayedGenre = film?.genre?.takeIf { it.isNotBlank() } ?: "Unknown genre"
    val displayedYear = film?.annee?.takeIf { it > 0 }?.toString() ?: "N/A"
    
    // Using a Column as the root ensures the Top Bar and the Content Area are physically separated
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fixed Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.disney_logo_white),
                contentDescription = "Disney Logo",
                modifier = Modifier.height(40.dp)
            )

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Image(
                painter = painterResource(id = pfpResId),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onProfileClick() },
                contentScale = ContentScale.Crop
            )
        }

        // Scrollable area and Bottom Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Movie Poster
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(0.7f),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    when {
                        posterUrl != null -> {
                            AsyncImage(
                                model = posterUrl,
                                contentDescription = "Movie Poster",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        isPosterLoading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                        else -> {
                            Image(
                                painter = painterResource(id = R.drawable.pfp_mickey),
                                contentDescription = "Movie Poster Placeholder",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                if (posterError != null && !isFilmLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = posterError,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(onClick = onRetryPosterLoad) {
                        Text(text = "Retry poster")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isFilmLoading -> {
                        Text(
                            text = "Loading film...",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    filmError != null -> {
                        Text(
                            text = "Failed to load film: $filmError",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        TextButton(onClick = onRetryFilmLoad) {
                            Text(text = "Retry")
                        }
                    }
                    isFilmNotFound -> {
                        Text(
                            text = "No film found for UUID: $filmUuid",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        Text(
                            text = displayedTitle,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 32.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Tag(text = displayedGenre)
                            Tag(text = displayedYear)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Items
                Column(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionItem(icon = Icons.Default.Add, text = "Want to watch")
                    ActionItem(icon = Icons.Default.RemoveRedEye, text = "Watched")
                    ActionItem(icon = Icons.Default.RadioButtonChecked, text = "Have a DVD / BlueRay")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { trailerUrl?.let(onOpenTrailer) },
                    enabled = trailerUrl != null && !isTrailerLoading
                ) {
                    Text(if (isTrailerLoading) "Loading trailer..." else "Watch trailer")
                }

                if (trailerError != null && !isFilmLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = trailerError,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(onClick = onRetryTrailerLoad) {
                        Text(text = "Retry trailer")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Marie Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pfp_cats),
                            contentDescription = "Marie",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Marie wants to get rid of their copy",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AppBottomNavBar(
    currentScreen: AppScreen,
    onHomeClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onFavoritesClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .height(60.dp)
                .weight(0.7f)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onHomeClick) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (currentScreen == AppScreen.Home) Color(0xFF1A237E) else Color.Black
                    )
                }
                IconButton(onClick = onCategoriesClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        contentDescription = "List",
                        tint = if (currentScreen == AppScreen.Categories) Color(0xFF1A237E) else Color.Black
                    )
                }
                IconButton(onClick = onFavoritesClick) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorites", tint = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        FloatingActionButton(
            onClick = onSearchClick,
            containerColor = Color.White,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier.size(60.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
    }
}

@Composable
fun Tag(text: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ActionItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp
        )
    }
}
