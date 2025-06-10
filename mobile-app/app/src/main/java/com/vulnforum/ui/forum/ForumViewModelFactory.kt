package com.vulnforum.ui.forum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vulnforum.network.ArticleService
import com.vulnforum.ui.forum.ForumViewModel


class ForumViewModelFactory(
    private val service: ArticleService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForumViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForumViewModel(service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
