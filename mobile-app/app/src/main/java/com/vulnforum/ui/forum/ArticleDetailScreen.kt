package com.vulnforum.ui.forum

import android.graphics.fonts.FontStyle
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vulnforum.data.Article
import com.vulnforum.data.Comment
import com.vulnforum.network.ApiClient
import com.vulnforum.network.ArticleService
import com.vulnforum.network.CommentService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




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
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> selectedImageUri = uri }
    )

    LaunchedEffect(articleId) {
        isLoading = true
        try {
            val articles = articleService.getArticles()
            article = articles.find { it.id == articleId }

            commentService.getCommentsForArticle(articleId)
                .enqueue(object : Callback<List<Comment>> {
                    override fun onResponse(
                        call: Call<List<Comment>>,
                        response: Response<List<Comment>>
                    ) {
                        if (response.isSuccessful) {
                            comments = response.body() ?: emptyList()
                        }
                        isLoading = false
                    }

                    override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                        error = t.message
                        isLoading = false
                    }
                })

        } catch (e: Exception) {
            error = e.message
            isLoading = false
        }
    }

    fun refreshComments() {
        isLoading = true
        commentService.getCommentsForArticle(articleId).enqueue(object : Callback<List<Comment>> {
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if (response.isSuccessful) {
                    comments = response.body() ?: emptyList()
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                error = t.message
                isLoading = false
            }
        })
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(article?.title ?: "Artykuł") },
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
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                error != null -> {
                    Text("Wystąpił błąd: $error", color = MaterialTheme.colorScheme.error)
                }

                article != null -> {
                    Text(
                        text = article!!.content,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Komentarze (${comments.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { commentsExpanded = !commentsExpanded }
                            .padding(vertical = 8.dp)
                    )

                    if (commentsExpanded) {
                        if (comments.isEmpty()) {
                            Text("Brak komentarzy")
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f, fill = false)
                            ) {
                                items(comments) { comment ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(comment.text)
                                            comment.filePath?.let { path ->
                                                val imageUrl = "http://yourserver.com$path"
                                                AsyncImage(
                                                    model = imageUrl,
                                                    contentDescription = "Obraz w komentarzu",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(150.dp)
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

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Dodaj komentarz", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("Treść komentarza") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        singleLine = false,
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    selectedImageUri?.let { uri ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Wybrane zdjęcie:", style = MaterialTheme.typography.bodyMedium)
                        AsyncImage(
                            model = uri,
                            contentDescription = "Wybrane zdjęcie",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            imagePickerLauncher.launch("image/*")
                        }) {
                            Text("Wybierz zdjęcie")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

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

                                val contentResolver = context.contentResolver
                                val fileBytes = selectedImageUri?.let { uri ->
                                    contentResolver.openInputStream(uri)?.readBytes()
                                }

                                val filePart = fileBytes?.let {
                                    val reqFile = it.toRequestBody("image/*".toMediaTypeOrNull())
                                    MultipartBody.Part.createFormData("file", "upload.jpg", reqFile)
                                }

                                val textPart =
                                    commentText.toRequestBody("text/plain".toMediaTypeOrNull())

                                commentService.postComment(articleId, textPart, filePart)
                                    .enqueue(object : Callback<Void> {
                                        override fun onResponse(
                                            call: Call<Void>,
                                            response: Response<Void>
                                        ) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Komentarz dodany",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                commentText = ""
                                                selectedImageUri = null
                                                refreshComments()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Błąd dodawania komentarza",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<Void>, t: Throwable) {
                                            Toast.makeText(
                                                context,
                                                "Błąd sieci",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }
                        ) {
                            Text("Dodaj komentarz")
                        }
                    }
                }
            }
        }
    }
}

