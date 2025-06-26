package com.vulnforum

import android.os.Bundle
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.vulnforum.ui.forum.ArticleDetailScreen
import com.vulnforum.ui.screens.ForumScreen
import com.vulnforum.ui.forum.PaywallScreen
import com.vulnforum.ui.messages.ComposeMessageScreen
import com.vulnforum.ui.screens.AdminScreen
import com.vulnforum.ui.screens.ChallengesScreen
import com.vulnforum.ui.screens.LoginScreen
import com.vulnforum.ui.screens.HomeScreen
import com.vulnforum.ui.screens.LogoutScreen
import com.vulnforum.ui.screens.MessagesScreen
import com.vulnforum.ui.screens.RegisterScreen
import com.vulnforum.ui.screens.WalletScreen
import com.vulnforum.ui.theme.VulnForumTheme
import com.vulnforum.util.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val context = LocalContext.current


                val onLogoutAction: () -> Unit = {
                    val appCtx = context.applicationContext


                    SessionManager(appCtx).clear()


                    WebView(appCtx).clearCache(true)
                    WebView(appCtx).clearHistory()
                    WebStorage.getInstance().deleteAllData()


                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }

                NavHost(navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }

                    composable("home") {
                        val sessionManager = remember { SessionManager(context) }
                        HomeScreen(
                            navController = navController,
                            username = sessionManager.getUsername().toString(),
                            onLogout = onLogoutAction
                        )
                    }

                    composable("forum") { ForumScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("admin_panel") { AdminScreen(navController) }
                    composable("wallet") { WalletScreen(navController) }
                    composable("compose_message") { ComposeMessageScreen(navController) }
                    composable("messages") {
                        MessagesScreen(
                            navController = navController,
                            onComposeClick = { navController.navigate("compose_message") }
                        )
                    }
                    composable("paywall/{articleId}") { backStackEntry ->
                        val articleId = backStackEntry.arguments?.getString("articleId")?.toInt() ?: 0
                        PaywallScreen(navController, articleId)
                    }
                    composable("article/{articleId}") { backStackEntry ->
                        val articleId = backStackEntry.arguments?.getString("articleId")?.toInt() ?: 0
                        ArticleDetailScreen(articleId, navController)
                    }
                    composable("challenges") { ChallengesScreen(navController) }


                }
            }
        }
    }
}
