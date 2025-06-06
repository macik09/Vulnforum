package com.vulnforum.ui.forum


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.vulnforum.data.Article
import com.vulnforum.data.dummyArticles

class ForumViewModel : ViewModel() {
    private val _articles = MutableStateFlow(dummyArticles)
    val articles: StateFlow<List<Article>> = _articles

    fun unlockArticle(articleId: Int) {
        _articles.value = _articles.value.map {
            if (it.id == articleId) it.copy(isUnlocked = true) else it
        }
    }
}
