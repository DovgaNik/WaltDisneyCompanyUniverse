package fr.isen.waltdisneycompanyuniverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import fr.isen.waltdisneycompanyuniverse.ui.AuthScreen
import fr.isen.waltdisneycompanyuniverse.ui.theme.WaltDisneyCompanyUniverseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaltDisneyCompanyUniverseTheme {
                var isAuthenticated by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isAuthenticated) {
                        Greeting(
                            name = "User",
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        AuthScreen(
                            onLoginSuccess = { isAuthenticated = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to Walt Disney Company Universe, $name!",
        modifier = modifier
    )
}
