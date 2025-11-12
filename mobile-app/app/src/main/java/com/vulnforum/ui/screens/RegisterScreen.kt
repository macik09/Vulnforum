package com.vulnforum.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vulnforum.data.RegisterRequest
import com.vulnforum.network.ApiClient
import com.vulnforum.network.AuthService
import com.vulnforum.ui.theme.AppBackground
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val authService = remember { ApiClient.getClient(context).create(AuthService::class.java) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    AppBackground {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Registration",
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
                        onValueChange = {
                            password = it
                            if (passwordError && it.length >= 6) {
                                passwordError = false
                            }
                        },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        singleLine = true,
                        isError = passwordError,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (passwordError) {
                        Text(
                            "Password must be at least 6 characters long",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            if (password.length < 6) {
                                passwordError = true
                                return@Button
                            }

                            val request = RegisterRequest(username, password)
                            authService.register(request).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                                        navController.navigate("login")
                                    } else {
                                        Toast.makeText(context, "Registration error", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                                }
                            })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Register")
                    }

                    TextButton(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Already have an account? Log in")
                    }
                }
            }
        }
    }
}
