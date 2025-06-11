package com.vulnforum.ui.forum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vulnforum.network.ArticleService
import com.vulnforum.network.WalletService
import com.vulnforum.ui.forum.ForumViewModel


class ForumViewModelFactory(
    private val articleService: ArticleService,
    private val walletService: WalletService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForumViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForumViewModel(articleService, walletService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
