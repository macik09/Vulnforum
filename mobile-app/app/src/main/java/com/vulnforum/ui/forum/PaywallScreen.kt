package com.vulnforum.ui.forum


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vulnforum.network.ApiClient
import com.vulnforum.network.ArticleService
import com.vulnforum.network.WalletService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
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

    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ten artykuł kosztuje 10 vulndolców.\nAby uzyskać dostęp, kliknij przycisk poniżej.")
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            viewModel.unlockArticle(articleId)
                            isLoading = false
                            navController.navigate("article/$articleId") {
                                popUpTo("forum") { inclusive = false }
                            }
                        }
                    }) {
                        Text("Zapłać")
                    }
                }
            }
        }
    }
}
