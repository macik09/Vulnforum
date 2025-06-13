package com.vulnforum.ui.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vulnforum.network.WalletService


class WalletViewModelFactory(private val walletService: WalletService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalletViewModel(walletService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}