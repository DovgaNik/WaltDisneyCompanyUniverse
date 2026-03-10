package fr.isen.waltdisneycompanyuniverse.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import fr.isen.waltdisneycompanyuniverse.R

@Composable
fun AuthScreen(
    onAuthSuccess: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()

    fun mapFirebaseError(exception: Exception?, isLoginMode: Boolean): String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> "The password is too weak. Please use at least 6 characters."
            is FirebaseAuthUserCollisionException -> "An account already exists with this email."
            is FirebaseAuthInvalidUserException -> "No account found with this email."
            is FirebaseAuthInvalidCredentialsException -> {
                if (isLoginMode) "Invalid email or password."
                else "The email address is badly formatted."
            }
            else -> "An unexpected error occurred. Please try again."
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.disney_logo_white),
            contentDescription = "Disney Logo",
            modifier = Modifier
                .width(240.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = if (isLogin) "Log in" else "Sign up",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        TextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            placeholder = { Text("Email", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            placeholder = { Text("Password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    if (isLogin) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onAuthSuccess(false)
                                } else {
                                    errorMessage = mapFirebaseError(task.exception, true)
                                }
                            }
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onAuthSuccess(true)
                                } else {
                                    errorMessage = mapFirebaseError(task.exception, false)
                                }
                            }
                    }
                } else {
                    errorMessage = "Please fill in all fields"
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB4C5E4),
                contentColor = Color.Black
            )
        ) {
            Text(
                if (isLogin) "Login" else "Sign Up",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {
            isLogin = !isLogin
            errorMessage = null
        }) {
            Text(
                if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Log In",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}
