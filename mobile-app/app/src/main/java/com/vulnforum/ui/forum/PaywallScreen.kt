package com.vulnforum.ui.forum

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    navController: NavController,
    articleId: Int,
    viewModel: ForumViewModel = viewModel()
) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Ten artykuł jest płatny.\nAby uzyskać dostęp, kliknij przycisk poniżej.")
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                viewModel.unlockArticle(articleId)
                navController.navigate("article/$articleId") {
                    popUpTo("forum") { inclusive = false }
                }
            }) {
                Text("Zapłać (atrapa)")
            }
        }
    }
}
