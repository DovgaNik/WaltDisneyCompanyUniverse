package fr.isen.waltdisneycompanyuniverse.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fr.isen.waltdisneycompanyuniverse.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private suspend fun fetchPosterUrlFromTmdb(title: String): String? {
    val apiKey = BuildConfig.TMDB_API_KEY.trim()
    if (apiKey.isBlank()) return null

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
                if (posterPath.isNotBlank() && posterPath != "null") {
                    return@withContext "https://image.tmdb.org/t/p/w342$posterPath"
                }
            }
            null
        } finally {
            connection.disconnect()
        }
    }
}

@Composable
fun UnifiedListItemCard(
    title: String,
    subtitle: String? = null,
    imageUrl: String? = null,
    posterTitle: String? = null,
    onClick: () -> Unit
) {
    var resolvedPosterUrl by remember(title, imageUrl, posterTitle) { mutableStateOf(imageUrl) }

    LaunchedEffect(imageUrl, posterTitle) {
        if (!imageUrl.isNullOrBlank()) {
            resolvedPosterUrl = imageUrl
            return@LaunchedEffect
        }

        val normalizedTitle = posterTitle?.trim().orEmpty()
        if (normalizedTitle.isBlank()) {
            resolvedPosterUrl = null
            return@LaunchedEffect
        }

        resolvedPosterUrl = try {
            fetchPosterUrlFromTmdb(normalizedTitle)
        } catch (_: Exception) {
            null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!resolvedPosterUrl.isNullOrBlank()) {
                AsyncImage(
                    model = resolvedPosterUrl,
                    contentDescription = "Poster for $title",
                    modifier = Modifier
                        .size(width = 76.dp, height = 112.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF151515)
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF151515)
                    )
                }
            }
        }
    }
}

