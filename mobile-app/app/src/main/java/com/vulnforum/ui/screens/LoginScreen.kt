package com.vulnforum.ui.screens

import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vulnforum.data.LoginRequest
import com.vulnforum.data.LoginResponse
import com.vulnforum.network.ApiClient
import com.vulnforum.network.AuthService
import com.vulnforum.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.vulnforum.R
import com.vulnforum.ui.theme.AppBackground
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authService = remember { ApiClient.getClient(context).create(AuthService::class.java) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    AppBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = R.drawable.vulnforum),
                contentDescription = "Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.08f)
                    .blur(6.dp)
            )


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Login",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val request = LoginRequest(username, password)
                                authService.login(request)
                                    .enqueue(object : Callback<LoginResponse> {
                                        override fun onResponse(
                                            call: Call<LoginResponse>,
                                            response: Response<LoginResponse>
                                        ) {
                                            if (response.isSuccessful) {
                                                val body = response.body()
                                                val token = body?.token ?: return
                                                val role = body.role
                                                val balance = body.balance
                                                val file = File(context.getExternalFilesDir(null), "app_data_vulnforum.txt")

                                                try {
                                                    FileOutputStream(file).use { fos ->
                                                        fos.write(password.toByteArray())
                                                    }
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                }
                                                sessionManager.saveSession(
                                                    token,
                                                    username,
                                                    role,
                                                    balance
                                                )
                                                navController.navigate("home") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Invalid login credentials",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<LoginResponse>,
                                            t: Throwable
                                        ) {
                                            Toast.makeText(
                                                context,
                                                "Network error",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log in")
                        }

                        TextButton(
                            onClick = { navController.navigate("register") },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Don't have an account? Register")
                        }
                    }
                }
            }
        }
    }

}
