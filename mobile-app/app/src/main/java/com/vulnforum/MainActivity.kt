package com.vulnforum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.vulnforum.ui.forum.ArticleDetailScreen
import com.vulnforum.ui.forum.ForumScreen
import com.vulnforum.ui.forum.PaywallScreen
import com.vulnforum.ui.messages.ComposeMessageScreen
import com.vulnforum.ui.screens.ChallengesScreen
import com.vulnforum.ui.screens.LoginScreen
import com.vulnforum.ui.screens.HomeScreen
import com.vulnforum.ui.screens.LogoutScreen
import com.vulnforum.ui.screens.MessagesScreen
import com.vulnforum.ui.screens.WalletScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("home") {
                    HomeScreen(
                        navController = navController,
                       // username = "JanKowalski",  // możesz dynamicznie podać nazwę użytkownika
                        onLogout = {
                            // tutaj logika wylogowania, np. czyszczenie sesji i powrót do loginu:
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }
                composable("forum") { ForumScreen(navController) }
                composable("wallet") { WalletScreen(navController) }
                composable("compose_message") { ComposeMessageScreen(navController = navController) }
                composable("messages") {
                    MessagesScreen(
                        navController = navController,
                        onComposeClick = {
                            // Co się dzieje po kliknięciu? Np. nawigacja do ekranu tworzenia nowej wiadomości:
                            navController.navigate("compose_message")
                        }
                    )
                }
                composable("forum") { ForumScreen(navController) }

                composable("paywall/{articleId}") { backStackEntry ->
                    val articleId = backStackEntry.arguments?.getString("articleId")?.toInt() ?: 0
                    PaywallScreen(navController, articleId)
                }

                composable("article/{articleId}") { backStackEntry ->
                    val articleId = backStackEntry.arguments?.getString("articleId")?.toInt() ?: 0
                    ArticleDetailScreen(articleId)
                }
                composable("challenges") { ChallengesScreen(navController) }

                composable("logout") { LogoutScreen(navController) }
            }
        }
    }
}
