package com.vulnforum.ui.forum


import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vulnforum.data.Article
import com.vulnforum.data.Comment
import com.vulnforum.network.ApiClient
import com.vulnforum.network.ArticleService
import com.vulnforum.network.CommentService
import com.vulnforum.ui.theme.AppBackground
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.vulnforum.util.SessionManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val apiClient = remember { ApiClient.getClient(context) }
    val articleService = remember { apiClient.create(ArticleService::class.java) }
    val commentService = remember { apiClient.create(CommentService::class.java) }

    var article by remember { mutableStateOf<Article?>(null) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var commentsExpanded by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    fun refreshComments() {
        isLoading = true
        commentService.getCommentsForArticle(articleId).enqueue(object : Callback<List<Comment>> {
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                comments = response.body() ?: emptyList()
                isLoading = false
            }

            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                error = t.message
                isLoading = false
            }
        })
    }

    LaunchedEffect(articleId) {
        isLoading = true
        try {
            article = articleService.getArticles().find { it.id == articleId }
            refreshComments()
        } catch (e: Exception) {
            error = e.message
            isLoading = false
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, "Błąd: $it", Toast.LENGTH_LONG).show()
            error = null
        }
    }

    AppBackground {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(
                            article?.title ?: "Artykuł",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                when {
                    isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    error != null -> Text("Błąd: $error", color = MaterialTheme.colorScheme.error)
                    article != null -> {
                        Text(
                            text = article!!.content,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))
                        Divider()
                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { commentsExpanded = !commentsExpanded },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Komentarze (${comments.size})",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (commentsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }

                        AnimatedVisibility(commentsExpanded) {
                            Column {
                                if (comments.isEmpty()) {
                                    Text("Brak komentarzy", style = MaterialTheme.typography.bodyMedium)
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 300.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(comments) { comment ->
                                            Card(
                                                shape = RoundedCornerShape(12.dp),
                                                elevation = CardDefaults.cardElevation(4.dp),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                            ) {
                                                Column(Modifier.padding(12.dp)) {
                                                    AndroidView(
                                                        factory = { ctx ->
                                                            WebView(ctx).apply {
                                                                settings.javaScriptEnabled = true
                                                                settings.domStorageEnabled = true
                                                                settings.allowContentAccess = true
                                                                settings.allowFileAccess = true
                                                                settings.allowFileAccessFromFileURLs = true
                                                                settings.allowUniversalAccessFromFileURLs = true
                                                                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                                                settings.databaseEnabled = true
                                                                settings.cacheMode = WebSettings.LOAD_NO_CACHE


                                                                webViewClient = object : WebViewClient() {
                                                                    override fun onPageFinished(view: WebView?, url: String?) {
                                                                        super.onPageFinished(view, url)
                                                                        Log.d("WebViewDebug", "onPageFinished dla URL: $url")
                                                                    }

                                                                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                                                        super.onReceivedError(view, request, error)
                                                                        Log.e("WebViewError", "Błąd ładowania WebView: ${error?.description}")
                                                                    }
                                                                }

                                                                loadUrl("about:blank")
                                                            }
                                                        },
                                                        update = { webView ->
                                                            val token = SessionManager(context).getToken()
                                                            val htmlContent = comment.text
                                                            val baseUrl = "http://localhost/"


                                                            webView.loadDataWithBaseURL(
                                                                baseUrl,
                                                                htmlContent,
                                                                "text/html",
                                                                "utf-8",
                                                                null
                                                            )


                                                            Handler(Looper.getMainLooper()).postDelayed({
                                                                if (token != null) {
                                                                    webView.evaluateJavascript("localStorage.setItem('token', '$token');") { result ->
                                                                        Log.d("WebViewDebug", "Wstrzyknięto token do localStorage.")

                                                                    }
                                                                } else {
                                                                    Log.w("WebViewDebug", "Token null, nie wstrzyknięto do localStorage.")
                                                                }
                                                            }, 150)

                                                        },
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(60.dp)
                                                    )

                                                    comment.filePath?.let { path ->
                                                        AsyncImage(
                                                            model = "http://yourserver.com$path",
                                                            contentDescription = null,
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(160.dp)
                                                                .clip(RoundedCornerShape(8.dp))
                                                                .padding(top = 8.dp),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        Divider()
                        Spacer(Modifier.height(16.dp))

                        Text("Dodaj komentarz", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            label = { Text("Treść komentarza") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (commentText.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Komentarz nie może być pusty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                val textPart = commentText.toRequestBody("text/plain".toMediaTypeOrNull())

                                commentService.postComment(articleId, textPart, null)
                                    .enqueue(object : Callback<Void> {
                                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Dodano komentarz", Toast.LENGTH_SHORT).show()
                                                commentText = ""
                                                refreshComments()
                                            } else {
                                                Toast.makeText(context, "Błąd zapisu", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<Void>, t: Throwable) {
                                            Toast.makeText(context, "Błąd sieci", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Dodaj komentarz")
                        }
                    }
                }
            }
        }
    }
}