package com.vulnforum.ui.wallet


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vulnforum.network.AddFundsRequest
import com.vulnforum.network.WalletService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletViewModel(private val walletService: WalletService) : ViewModel() {

    private val _balance = MutableStateFlow<Float?>(null) // null na początku, bo nie pobraliśmy jeszcze
    val balance: StateFlow<Float?> = _balance.asStateFlow()

    init {
        fetchBalance() // Pobierz saldo od razu po utworzeniu ViewModelu
    }

    fun fetchBalance() {
        viewModelScope.launch {
            try {
                val response = walletService.getWalletBalance()
                _balance.value = response.balance
            } catch (e: Exception) {
                // Obsługa błędów, np. wyświetlenie Toast lub Log.e
                println("Error fetching balance: ${e.message}")
                _balance.value = null // W przypadku błędu możesz zresetować lub pozostawić poprzednie
            }
        }
    }

    fun addFunds(amount: Float) {
        viewModelScope.launch {
            try {
                val request = AddFundsRequest(amount)
                val response = walletService.addFunds(request)
                // --- KOREKTA TUTAJ ---
                // Użyj 'new_balance', ponieważ tak nazwaliśmy to pole w AddFundsResponse
                _balance.value = response.new_balance

                // Możesz również wysłać zdarzenie do UI, aby wyświetlić Toast, np. "Środki dodane!"
            } catch (e: Exception) {
                // W przypadku błędu (np. brak sieci, błąd serwera, za mało środków na backendzie):
                // Możesz zalogować błąd: Log.e("WalletViewModel", "Błąd podczas dodawania środków", e)
                println("Błąd podczas dodawania środków: ${e.message}") // Wyświetlenie w konsoli do debugowania
                // Możesz również zaimplementować lepszą obsługę błędów, np. wyświetlić SnackBar w UI.
            }
        }
    }
}
/*
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

    fun purchaseVulndolcs(amount: Float) {
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
    */


