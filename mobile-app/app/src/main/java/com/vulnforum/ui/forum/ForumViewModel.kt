package com.vulnforum.ui.forum



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.vulnforum.data.Article
import com.vulnforum.network.ArticleService
import com.vulnforum.network.PurchaseRequest
import com.vulnforum.network.WalletService
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ForumViewModel(
    private val articleService: ArticleService,
    private val walletService: WalletService
) : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        getArticles()
    }

    fun unlockArticle(articleId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val articlePrice = 10000f
                val purchaseResponse = walletService.purchase(PurchaseRequest(amount = articlePrice))

                if (purchaseResponse.new_balance >= 0) {
                    val response = articleService.unlockArticle(articleId)
                    if (response.isSuccessful) {
                        getArticles()
                        onSuccess()
                    } else {
                        _errorMessage.value = "Nie udało się odblokować artykułu."
                    }
                } else {
                    _errorMessage.value = "Za mało vulndolców na zakup artykułu."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Błąd zakupu: ${e.message}"
            }
        }
    }

    fun getArticles() {
        viewModelScope.launch {
            try {
                val fetchedArticles = articleService.getArticles()
                _articles.value = fetchedArticles
            } catch (e: Exception) {
                _errorMessage.value = "Nie udało się pobrać artykułów."
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
