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
import retrofit2.HttpException
import java.io.IOException


class ForumViewModel(
    private val articleService: ArticleService,
    private val walletService: WalletService
) : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var lastReceivedAccessKey: String? = null

    init {
        getArticles()
    }

    fun unlockArticle(articleId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _errorMessage.value = null

            try {
                val articlePrice = 10000f
                val purchaseRequest = PurchaseRequest(amount = articlePrice)

                val purchaseRetrofitResponse = walletService.purchase(purchaseRequest)

                if (purchaseRetrofitResponse.isSuccessful) {
                    lastReceivedAccessKey = purchaseRetrofitResponse.headers()["X-Access-Key"]
                    val purchaseBody = purchaseRetrofitResponse.body()
                    val isBalanceSufficient = purchaseBody?.new_balance?.let { it >= 0 } ?: false

                    if (lastReceivedAccessKey != null && isBalanceSufficient) {
                        val unlockResponse = articleService.unlockArticle(articleId, lastReceivedAccessKey!!)

                        if (unlockResponse.isSuccessful) {
                            getArticles()
                            onSuccess()
                        } else {
                            val errorBody = unlockResponse.errorBody()?.string()
                            _errorMessage.value = "Nie udało się odblokować artykułu: ${unlockResponse.code()} ${errorBody ?: "Nieznany błąd"}"
                        }
                    } else if (lastReceivedAccessKey == null) {
                        _errorMessage.value = "Błąd: Serwer nie zwrócił klucza dostępu po zakupie."
                    } else {
                        _errorMessage.value = purchaseBody?.message ?: "Za mało vulndolców na zakup artykułu."
                    }
                } else {
                    val errorBody = purchaseRetrofitResponse.errorBody()?.string()
                    _errorMessage.value = "Błąd zakupu: ${purchaseRetrofitResponse.code()} ${errorBody ?: "Nieznany błąd"}"
                }
            } catch (e: HttpException) {
                _errorMessage.value = "Błąd komunikacji HTTP: ${e.code()} - ${e.message()}"
            } catch (e: IOException) {
                _errorMessage.value = "Błąd połączenia sieciowego: ${e.message}"
            } catch (e: Exception) {
                _errorMessage.value = "Wystąpił nieoczekiwany błąd: ${e.message}"
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
