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


class ForumViewModel(private val articleService: ArticleService, private val walletService: WalletService) : ViewModel(){



    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()



    init {

        getArticles()
    }

    fun unlockArticle(articleId: Int, onSuccess: () -> Unit = {}) {

        viewModelScope.launch {
            try {
                val articlePrice = 10f
                val purchaseResponse = walletService.purchase(PurchaseRequest(amount = articlePrice))

                if (purchaseResponse.new_balance >= 0) {
                    val response = articleService.unlockArticle(articleId)
                    if (response.isSuccessful) {
                        getArticles()
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }



    fun getArticles() {
        viewModelScope.launch {
            try {
                val fetchedArticles = articleService.getArticles()
                fetchedArticles.forEach {
                }
                _articles.value = fetchedArticles
            } catch (e: Exception) {
            }
        }
    }
}