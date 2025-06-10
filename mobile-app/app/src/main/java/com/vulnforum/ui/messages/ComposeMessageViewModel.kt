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

class ComposeMessageViewModel(private val messageService: MessageService) : ViewModel() {

    private val _sendStatus = MutableStateFlow<String?>(null)
    val sendStatus: StateFlow<String?> = _sendStatus

    fun sendMessage(sender: String, recipient: String, content: String) {
        val message = Message(
            id = 0,  // id może być 0 lub null, backend nada prawidłowe
            sender = sender,
            recipient = recipient,
            content = content,
            timestamp = ""
        )
        viewModelScope.launch {
            try {
                val response = messageService.sendMessage(message).awaitResponse()
                if (response.isSuccessful) {
                    _sendStatus.value = "Wiadomość wysłana!"
                } else {
                    _sendStatus.value = "Błąd wysyłania"
                }
            } catch (e: Exception) {
                _sendStatus.value = "Błąd sieci: ${e.message}"
            }
        }
    }

    fun clearStatus() {
        _sendStatus.value = null
    }
}