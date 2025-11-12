@file:OptIn(ExperimentalMaterial3Api::class)

package com.vulnforum.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import com.vulnforum.data.Article
import com.vulnforum.network.ApiClient
import com.vulnforum.network.ArticleService
import com.vulnforum.network.WalletService
import com.vulnforum.ui.forum.ForumViewModel
import com.vulnforum.ui.forum.ForumViewModelFactory
import com.vulnforum.ui.theme.AppBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(navController: NavController) {
    val context = LocalContext.current
    val owner = LocalViewModelStoreOwner.current

    val articleService = remember {
        ApiClient.getClient(context).create(ArticleService::class.java)
    }
    val walletService = remember {
        ApiClient.getClient(context).create(WalletService::class.java)
    }
    val factory = remember { ForumViewModelFactory(articleService, walletService) }

    val viewModel: ForumViewModel = viewModel(
        factory = factory,
        viewModelStoreOwner = owner ?: error("No ViewModelStoreOwner found")
    )

    val articles by viewModel.articles.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.getArticles()
    }
    AppBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Articles", style = MaterialTheme.typography.headlineSmall) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = Color.Transparent // <- allows AppBackground to be visible
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(items = articles, key = { it.id }) { article ->
                    ArticleCardStyled(
                        article = article,
                        onClick = {
                            val route = if (article.isPaid && !article.isUnlocked) {
                                "paywall/${article.id}"
                            } else {
                                "article/${article.id}"
                            }
                            navController.navigate(route)
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun ArticleCardStyled(
    article: Article,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))


            if (article.isPaid) {
                Text(
                    text = if (article.isUnlocked) "Paid" else "Premium",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (article.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = article.content.take(100) + "...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (article.isPaid) "" else "Free",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Click to ${if (article.isPaid && !article.isUnlocked) "unlock" else "read"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
