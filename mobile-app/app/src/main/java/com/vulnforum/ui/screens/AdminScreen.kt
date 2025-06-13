package com.vulnforum.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import com.vulnforum.data.AdminUser
import com.vulnforum.data.Article
import com.vulnforum.network.AdminService
import com.vulnforum.network.ApiClient
import com.vulnforum.network.ArticleService
import com.vulnforum.network.UpdateArticlePaymentStatusRequest
import kotlinx.coroutines.launch


@Composable
fun AdminScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var users by remember { mutableStateOf<List<AdminUser>>(emptyList()) }
    var articles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var articleStates by remember { mutableStateOf(mapOf<Int, Boolean>()) }

    val articleService = remember {
        ApiClient.getClient(context).create(ArticleService::class.java)
    }
    val adminService = remember {
        ApiClient.getClient(context).create(AdminService::class.java)
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                users = adminService.getAllUsers()
                articles = articleService.getArticles()
                articleStates = articles.associate { it.id to it.isPaid }
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                Text("👥 Użytkownicy", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
            }

            items(users) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("👤 ${user.username}")
                        Text("💰 Saldo: ${user.balance} vulndolców")
                        Text("📖 Odblokowane: ${user.unlocked_articles.joinToString()}")
                    }
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
                Text("📝 Artykuły", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
            }

            items(articles) { article ->
                val isPaid = articleStates[article.id] ?: false

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("📌 ${article.title}")
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💲 Płatny: ")
                            Switch(
                                checked = isPaid,
                                onCheckedChange = { newValue ->
                                    articleStates = articleStates.toMutableMap().also {
                                        it[article.id] = newValue
                                    }
                                    coroutineScope.launch {
                                        adminService.updateArticlePaymentStatus(article.id, UpdateArticlePaymentStatusRequest(is_paid = newValue))

                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
