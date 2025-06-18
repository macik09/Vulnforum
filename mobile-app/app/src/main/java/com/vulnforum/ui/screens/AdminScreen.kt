package com.vulnforum.ui.screens



import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.vulnforum.data.AdminUser
import com.vulnforum.data.Article
import com.vulnforum.network.AdminService
import com.vulnforum.network.ApiClient
import com.vulnforum.network.ArticleService
import com.vulnforum.network.UpdateArticlePaymentStatusRequest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
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

    fun loadData() {
        coroutineScope.launch {
            isLoading = true
            try {
                users = adminService.getAllUsers()
                articles = articleService.getArticles()
                articleStates = articles.associate { it.id to it.isPaid }
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadData() }

    if (isLoading) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 6.dp,
                modifier = Modifier.size(60.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                SectionHeader(icon = "👥", title = "Użytkownicy")
                Spacer(Modifier.height(12.dp))
            }

            items(users) { user ->
                GradientCard(
                    modifier = Modifier.padding(vertical = 6.dp),
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "👤 ${user.username}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "💰 Saldo: ${user.balance} vulndolców",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "📖 Odblokowane: ${if(user.unlocked_articles.isEmpty()) "Brak" else user.unlocked_articles.joinToString()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
                SectionHeader(icon = "📝", title = "Artykuły")
                Spacer(Modifier.height(12.dp))
            }

            items(articles) { article ->
                val isPaid = articleStates[article.id] ?: false
                GradientCard(
                    modifier = Modifier.padding(vertical = 6.dp),
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "📌 ${article.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Płatny:",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AnimatedSwitch(
                                checked = isPaid,
                                onCheckedChange = { newValue ->
                                    articleStates = articleStates.toMutableMap().also {
                                        it[article.id] = newValue
                                    }
                                    coroutineScope.launch {
                                        adminService.updateArticlePaymentStatus(
                                            article.id,
                                            UpdateArticlePaymentStatusRequest(is_paid = newValue)
                                        )
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

@Composable
fun SectionHeader(icon: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(colors),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(2.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                tonalElevation = 6.dp,
                shadowElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
    }
}

@Composable
fun AnimatedSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val transition = updateTransition(targetState = checked, label = "SwitchTransition")

    val thumbColor by transition.animateColor(label = "ThumbColor") { state ->
        if (state) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    }
    val trackColor by transition.animateColor(label = "TrackColor") { state ->
        if (state) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    }

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = thumbColor,
            uncheckedThumbColor = thumbColor,
            checkedTrackColor = trackColor,
            uncheckedTrackColor = trackColor
        ),
        modifier = Modifier.size(width = 48.dp, height = 28.dp)
    )
}
