package com.vulnforum.ui.wallet


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vulnforum.network.AddFundsRequest
import com.vulnforum.network.WalletService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class WalletViewModel(private val walletService: WalletService) : ViewModel() {

    private val _balance = MutableStateFlow<Float?>(null)
    val balance: StateFlow<Float?> = _balance.asStateFlow()

    init {
        fetchBalance()
    }

    fun fetchBalance() {
        viewModelScope.launch {
            try {
                val response = walletService.getWalletBalance()
                _balance.value = response.balance
            } catch (e: Exception) {

                println("Error fetching balance: ${e.message}")
                _balance.value = null
            }
        }
    }

    fun addFunds(amount: Float) {
        viewModelScope.launch {
            try {
                val nonce = UUID.randomUUID().toString()
                val request = AddFundsRequest(amount, nonce)
                val response = walletService.addFunds(request)

                _balance.value = response.new_balance


            } catch (e: Exception) {

                println("Błąd podczas dodawania środków: ${e.message}")

            }
        }
    }
}

