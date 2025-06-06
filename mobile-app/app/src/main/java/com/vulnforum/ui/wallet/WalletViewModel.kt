package com.vulnforum.ui.wallet

import androidx.lifecycle.ViewModel
import com.vulnforum.data.Article
import com.vulnforum.data.Wallet
import com.vulnforum.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WalletViewModel : ViewModel() {


    private val _wallet = MutableStateFlow(Wallet(balance = 5f)) // domyślnie 5 vulndolców
    val wallet: StateFlow<Wallet> = _wallet

    private val _articles = MutableStateFlow(
        listOf(
            Article(1, "Bezpłatny poradnik bezpieczeństwa", "Treść ogólnodostępna", false),
            Article(2, "Zaawansowana analiza podatności", "Treść płatna", true),
            Article(3, "Przewodnik po exploitach", "Treść płatna", true)
        )
    )
    val articles: StateFlow<List<Article>> = _articles

    fun purchaseVulndolcs(amount: Int) {
        _wallet.value = _wallet.value.copy(balance = _wallet.value.balance + amount)
    }

    fun unlockArticle(articleId: Int): Boolean {
        val wallet = _wallet.value
        val article = _articles.value.find { it.id == articleId } ?: return false

        if (!article.isPaid) return true
        if (wallet.unlockedArticleIds.contains(articleId)) return true
        if (wallet.balance < 3) return false // np. każdy artykuł kosztuje 3 vulndolce

        wallet.balance -= 3
        wallet.unlockedArticleIds.add(articleId)

        _wallet.value = wallet
        _articles.value = _articles.value.map {
            if (it.id == articleId) it.copy(isUnlocked = true) else it
        }

        return true
    }
}
