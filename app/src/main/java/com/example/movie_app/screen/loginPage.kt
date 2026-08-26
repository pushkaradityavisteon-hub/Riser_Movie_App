import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movie_app.screen.HomeScreen

@Composable
fun LoginScreen(navController: NavController) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("Movie_app", Context.MODE_PRIVATE)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Movie Rating App",
            fontSize = 32.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(50.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            placeholder = {
                Text("Username")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if(username.isNotBlank() && password.isNotBlank()){
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                    sharedPreferences.edit().putString("username", username).apply()
                    sharedPreferences.edit().putString("password", password).apply()
                    navController.navigate("Home")
                }
                
            }
        ) {
            Text("Login")
        }
    }
}

