package fr.isen.waltdisneycompanyuniverse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.waltdisneycompanyuniverse.ui.theme.DisneyBlue
import fr.isen.waltdisneycompanyuniverse.ui.theme.DisneyDeepBlue
import fr.isen.waltdisneycompanyuniverse.ui.theme.WaltDisneyCompanyUniverseTheme

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaltDisneyCompanyUniverseTheme(darkTheme = true) {
                val context = LocalContext.current
                var userName by remember { mutableStateOf("Username") }
                var selectedPfpIndex by remember { mutableIntStateOf(0) }
                val user = FirebaseAuth.getInstance().currentUser
                var email by remember { mutableStateOf(user?.email ?: "name@gmail.com") }

                var showNameDialog by remember { mutableStateOf(false) }
                var showEmailDialog by remember { mutableStateOf(false) }
                var showPasswordDialog by remember { mutableStateOf(false) }
                var showPfpDialog by remember { mutableStateOf(false) }

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

                LaunchedEffect(Unit) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) {
                        FirebaseDatabase.getInstance().reference.child("users").child(uid).child("persona")
                            .get().addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    userName = snapshot.child("username").getValue(String::class.java) ?: "Username"
                                    selectedPfpIndex = snapshot.child("pfp").getValue(Int::class.java) ?: 0
                                }
                            }
                    }
                }

                fun updateFirebase(key: String, value: Any) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
                    FirebaseDatabase.getInstance().reference.child("users").child(uid).child("persona")
                        .child(key).setValue(value)
                        .addOnSuccessListener {
                            Toast.makeText(context, "$key updated", Toast.LENGTH_SHORT).show()
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
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = Color.Transparent,
                            topBar = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    IconButton(onClick = { finish() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Image(
                                        painter = painterResource(id = R.drawable.disney_logo_white),
                                        contentDescription = "Disney Logo",
                                        modifier = Modifier.height(40.dp)
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize()
                                    .padding(horizontal = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(32.dp))

                                // Profile Section
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = profilePictures[selectedPfpIndex]),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(24.dp))
                                    Column {
                                        Text(
                                            text = userName,
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "Movies watched: 2003",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Text(
                                    text = "Change profile picture",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.clickable { showPfpDialog = true }
                                )

                                Spacer(modifier = Modifier.height(48.dp))

                                // Edit Fields
                                EditField(label = "Username", value = userName, onClick = { showNameDialog = true })
                                Spacer(modifier = Modifier.height(24.dp))
                                EditField(label = "Email", value = email, onClick = { showEmailDialog = true })
                                Spacer(modifier = Modifier.height(24.dp))
                                EditField(label = "Password", value = "********", onClick = { showPasswordDialog = true })

                                Spacer(modifier = Modifier.height(32.dp))

                                // Log out button as plain text
                                TextButton(
                                    onClick = {
                                        FirebaseAuth.getInstance().signOut()
                                        val intent = Intent(context, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        context.startActivity(intent)
                                        finish()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Log Out",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        // Dialogs
                        if (showNameDialog) {
                            var newName by remember { mutableStateOf(userName) }
                            AlertDialog(
                                onDismissRequest = { showNameDialog = false },
                                title = { Text("Edit Name") },
                                text = {
                                    TextField(value = newName, onValueChange = { newName = it }, label = { Text("Name") })
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        userName = newName
                                        updateFirebase("username", newName)
                                        showNameDialog = false
                                    }) { Text("Save") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showNameDialog = false }) { Text("Cancel") }
                                }
                            )
                        }

                        if (showEmailDialog) {
                            var newEmail by remember { mutableStateOf(email) }
                            var currentPassword by remember { mutableStateOf("") }
                            AlertDialog(
                                onDismissRequest = { showEmailDialog = false },
                                title = { Text("Edit Email") },
                                text = {
                                    Column {
                                        TextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("New Email") })
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextField(value = currentPassword, onValueChange = { currentPassword = it }, label = { Text("Current Password (required)") })
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        val credential = EmailAuthProvider.getCredential(user?.email!!, currentPassword)
                                        user.reauthenticate(credential).addOnSuccessListener {
                                            user.verifyBeforeUpdateEmail(newEmail).addOnSuccessListener {
                                                Toast.makeText(context, "Verification email sent", Toast.LENGTH_SHORT).show()
                                                showEmailDialog = false
                                            }.addOnFailureListener {
                                                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }.addOnFailureListener {
                                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }) { Text("Save") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showEmailDialog = false }) { Text("Cancel") }
                                }
                            )
                        }

                        if (showPasswordDialog) {
                            var newPassword by remember { mutableStateOf("") }
                            var currentPassword by remember { mutableStateOf("") }
                            AlertDialog(
                                onDismissRequest = { showPasswordDialog = false },
                                title = { Text("Change Password") },
                                text = {
                                    Column {
                                        TextField(value = currentPassword, onValueChange = { currentPassword = it }, label = { Text("Current Password") })
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("New Password") })
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        val credential = EmailAuthProvider.getCredential(user?.email!!, currentPassword)
                                        user.reauthenticate(credential).addOnSuccessListener {
                                            user.updatePassword(newPassword).addOnSuccessListener {
                                                Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                                                showPasswordDialog = false
                                            }.addOnFailureListener {
                                                Toast.makeText(context, "Error updating password", Toast.LENGTH_SHORT).show()
                                            }
                                        }.addOnFailureListener {
                                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }) { Text("Save") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showPasswordDialog = false }) { Text("Cancel") }
                                }
                            )
                        }

                        if (showPfpDialog) {
                            AlertDialog(
                                onDismissRequest = { showPfpDialog = false },
                                title = { Text("Select Profile Picture") },
                                text = {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3),
                                        modifier = Modifier.height(300.dp)
                                    ) {
                                        itemsIndexed(profilePictures) { index, resId ->
                                            Image(
                                                painter = painterResource(id = resId),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .size(80.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        selectedPfpIndex = index
                                                        updateFirebase("pfp", index)
                                                        showPfpDialog = false
                                                    },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                },
                                confirmButton = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditField(label: String, value: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.6f)
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontSize = 18.sp
                )
            )
        }
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit",
            tint = Color.White.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(20.dp)
        )
    }
}
