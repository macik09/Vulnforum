package com.vulnforum.ui.forum

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp

@Composable
fun ForumScreen(navController: NavController, viewModel: ForumViewModel = viewModel()) {
    val articles by viewModel.articles.collectAsState()

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
