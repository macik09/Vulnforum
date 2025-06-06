package com.vulnforum.ui.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vulnforum.data.Message
import com.vulnforum.network.ApiClient
import com.vulnforum.network.MessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class MessagesViewModel : ViewModel() {

    private val messageService = ApiClient.getClient().create(MessageService::class.java)

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun loadMessages() {
        viewModelScope.launch {
            try {
                val response = messageService.getMessages().awaitResponse()
                if (response.isSuccessful) {
                    _messages.value = response.body() ?: emptyList()
                } else {
                    // Obsługa błędu np. logowanie lub toast
                }
            } catch (e: Exception) {
                // Obsługa wyjątku
            }
        }
    }
}