package com.vulnforum.ui.forum


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vulnforum.network.ApiClient
import com.vulnforum.network.ArticleService
import com.vulnforum.network.WalletService
import com.vulnforum.ui.theme.AppBackground
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    navController: NavController,
    articleId: Int
) {
    AppBackground {
        PaywallScreenContent(navController, articleId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreenContent(
    navController: NavController,
    articleId: Int
) {
    val context = LocalContext.current
    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModelStoreOwner found")

    val articleService = remember { ApiClient.getClient(context).create(ArticleService::class.java) }
    val walletService = remember { ApiClient.getClient(context).create(WalletService::class.java) }
    val factory = remember { ForumViewModelFactory(articleService, walletService) }

    val viewModel: ForumViewModel = viewModel(
        factory = factory,
        viewModelStoreOwner = owner
    )

    val errorMessage by viewModel.errorMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.clearError()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Błąd płatności", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Text(
                    "Nie masz wystarczającej ilości vulndolców, aby odblokować ten artykuł.\n" +
                            "Proszę doładuj portfel lub wybierz inny artykuł.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showErrorDialog = false
                    viewModel.clearError()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showErrorDialog = false
                    viewModel.clearError()
                    navController.popBackStack()
                }) {
                    Text("Anuluj")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Płatność") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()

                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp,
                    modifier = Modifier.size(64.dp)
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Text(
                        "Ten artykuł kosztuje",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "10 000 vulndolców",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Aby uzyskać dostęp, kliknij przycisk poniżej i zapłać.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(32.dp))


                    var pressed by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(targetValue = if (pressed) 0.95f else 1f)

                    Button(
                        onClick = {
                            isLoading = true
                            coroutineScope.launch {
                                viewModel.unlockArticle(articleId) {
                                    isLoading = false
                                    navController.navigate("article/$articleId") {
                                        popUpTo("forum") { inclusive = false }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            "Zapłać teraz",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}
