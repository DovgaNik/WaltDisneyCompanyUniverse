package fr.isen.waltdisneycompanyuniverse.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.waltdisneycompanyuniverse.R

val pronounsList = listOf("He/Him", "She/Her", "They/Them", "Prefer not to say", "Other")

fun saveUserToFirebase(username: String, pronouns: Int, pfp: Int) {
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid ?: run {
        Log.e("FirebaseSave", "No user logged in, cannot save data.")
        return
    }
    
    val database = FirebaseDatabase.getInstance().reference
    val userData = mapOf(
        "persona" to mapOf(
            "username" to username,
            "pronouns" to pronouns,
            "pfp" to pfp
        )
    )

    database.child("users").child(uid).setValue(userData)
        .addOnSuccessListener {
            Log.d("FirebaseSave", "User data successfully saved for UID: $uid")
        }
        .addOnFailureListener { e ->
            Log.e("FirebaseSave", "Error saving user data", e)
        }
}

@Composable
fun NameOnboardingScreen(
    onNameSubmitted: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "How should we call you?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Your name", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { if (name.isNotBlank()) onNameSubmitted(name) },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB4C5E4),
                contentColor = Color.Black
            )
        ) {
            Text("Next", fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronounsOnboardingScreen(
    onPronounsSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedPronouns by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "What are your pronouns?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedPronouns,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select pronouns", color = Color.Gray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                pronounsList.forEach { pronouns ->
                    DropdownMenuItem(
                        text = { Text(pronouns, color = Color.Black) },
                        onClick = {
                            selectedPronouns = pronouns
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { if (selectedPronouns.isNotEmpty()) onPronounsSelected(selectedPronouns) },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB4C5E4),
                contentColor = Color.Black
            ),
            enabled = selectedPronouns.isNotEmpty()
        ) {
            Text("Next", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProfilePictureOnboardingScreen(
    onFinish: (Int?) -> Unit
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val profilePictures: List<Int> = listOf(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose your\nprofile picture",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp
            ),
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "You'll be able to change it later",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(profilePictures) { index, resId ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(
                            width = if (selectedIndex == index) 3.dp else 0.dp,
                            color = if (selectedIndex == index) Color(0xFFB4C5E4) else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedIndex = index }
                ) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = "Profile Picture Option",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onFinish(selectedIndex) },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB4C5E4),
                contentColor = Color.Black
            )
        ) {
            Text("Sign up", fontWeight = FontWeight.Medium)
        }
    }
}
