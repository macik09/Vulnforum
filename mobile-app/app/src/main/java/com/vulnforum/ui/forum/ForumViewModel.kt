package com.vulnforum.ui.forum


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.vulnforum.data.Article
import com.vulnforum.network.ArticleService
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ForumViewModel(private val articleService: ArticleService) : ViewModel(){

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow() // Lepiej użyć asStateFlow()

    init {
        // Wywołaj getArticles() od razu, gdy ViewModel zostanie utworzony
        getArticles()
    }

    fun unlockArticle(articleId: Int) {
        viewModelScope.launch { // Pamiętaj o coroutine scope dla operacji asynchronicznych
            try {
                articleService.unlockArticle(articleId)
                // Opcjonalnie: odśwież listę artykułów po odblokowaniu, jeśli to ma wpływ
                // getArticles()
            } catch (e: Exception) {
                // obsługa błędu
            }
        }
    }

    fun getArticles() {
        viewModelScope.launch {
            try {
                val fetchedArticles = articleService.getArticles()
                _articles.value = fetchedArticles
            } catch (e: Exception) {
                // obsługa błędu, np. logowanie e.printStackTrace() lub wyświetlenie Toast
                println("Error fetching articles: ${e.message}") // Do debugowania
            }
        }
    }
}