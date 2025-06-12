package com.vulnforum.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vulnforum.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    username: String,
    navController: NavController,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val role = sessionManager.getRole()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Witaj $username w aplikacji VulnForum") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Wyloguj")
                    }
                    IconButton(onClick = { navController.navigate("wallet") }) {
                        Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Portfel")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text("Nawigacja", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LargeIconButton(
                        text = "Forum",
                        icon = Icons.Filled.Forum,
                        onClick = { navController.navigate("forum") }
                    )
                    LargeIconButton(
                        text = "Wiadomości",
                        icon = Icons.Filled.Message,
                        onClick = { navController.navigate("messages") }
                    )
                }

                if (role == "admin") {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Panel administratora", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    LargeIconButton(
                        text = "Panel Admina",
                        icon = Icons.Filled.AdminPanelSettings,
                        onClick = { navController.navigate("admin_panel") }
                    )
                }
            }
        }
    )
}

@Composable
fun LargeIconButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(140.dp)
            .clickable(onClick = onClick)
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = text,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}





