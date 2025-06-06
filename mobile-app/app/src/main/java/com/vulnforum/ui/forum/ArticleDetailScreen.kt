package com.vulnforum.ui.forum

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleId: Int,
    viewModel: ForumViewModel = viewModel()
) {
    val article = viewModel.articles.collectAsState().value.find { it.id == articleId }

    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text(article?.title ?: "Brak artykułu") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            article?.let {
                Text(it.content)
            } ?: Text("Nie znaleziono artykułu.")
        }
    }
}
