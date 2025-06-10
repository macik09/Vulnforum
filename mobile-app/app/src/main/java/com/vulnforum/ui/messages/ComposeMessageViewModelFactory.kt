package com.vulnforum.ui.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vulnforum.network.MessageService

class ComposeMessageViewModelFactory(
    private val messageService: MessageService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComposeMessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComposeMessageViewModel(messageService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
