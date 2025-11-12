package com.vulnforum.ui.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vulnforum.data.Message
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
            id = 0,  // The backend assigns the real ID
            sender = sender,
            recipient = recipient,
            content = content,
            timestamp = ""
        )

        viewModelScope.launch {
            try {
                val response = messageService.sendMessage(message).awaitResponse()
                _sendStatus.value = if (response.isSuccessful) {
                    "Message sent successfully!"
                } else {
                    "Failed to send message"
                }
            } catch (e: Exception) {
                _sendStatus.value = "Network error: ${e.message}"
            }
        }
    }

    fun clearStatus() {
        _sendStatus.value = null
    }
}