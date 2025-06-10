package com.vulnforum.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items

import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import com.vulnforum.network.ApiClient
import com.vulnforum.network.ArticleService
import com.vulnforum.ui.forum.ForumViewModel
import com.vulnforum.ui.forum.ForumViewModelFactory

@Composable
fun ForumScreen(navController: NavController) {
    val context = LocalContext.current
    val owner = LocalViewModelStoreOwner.current

    val service = remember {
        ApiClient.getClient(context).create(ArticleService::class.java)
    }
    val factory = remember { ForumViewModelFactory(service) }

    val viewModel: ForumViewModel = viewModel(
        factory = factory,
        viewModelStoreOwner = owner ?: error("No ViewModelStoreOwner found")
    )

    LaunchedEffect(Unit) {
        viewModel.getArticles()
    }

    val articlesState = viewModel.articles.collectAsState(initial = emptyList())
    val articles = articlesState.value

    LazyColumn {
        items(articles) { article ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable {
                        if (article.isPaid && !article.isUnlocked) {
                            navController.navigate("paywall/${article.id}")
                        } else {
                            navController.navigate("article/${article.id}")
                        }
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(article.title, style = MaterialTheme.typography.titleMedium)
                    if (article.isPaid && !article.isUnlocked) {
                        Text("🔒 Treść płatna", color = MaterialTheme.colorScheme.error)
                    } else {
                        Text(article.content.take(100) + "...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

