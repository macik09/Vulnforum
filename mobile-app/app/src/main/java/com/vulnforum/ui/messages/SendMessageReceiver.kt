package com.vulnforum.ui.messages

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.vulnforum.data.Message
import com.vulnforum.network.ApiClient
import com.vulnforum.network.MessageService
import com.vulnforum.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendMessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val recipient = intent.getStringExtra("recipient")
        val content = intent.getStringExtra("content")

        if (recipient.isNullOrBlank() || content.isNullOrBlank()) {
            Log.e("SendMessageReceiver", "Brak danych")
            return
        }

        val sessionManager = SessionManager(context)
        val sender = sessionManager.getUsername() ?: "anonymous"

        val api = ApiClient.getClient(context).create(MessageService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val message = Message(
                    id = 0,
                    sender = sender,
                    recipient = recipient,
                    content = content,
                    timestamp = ""
                )
                val response = api.sendMessage(message).execute()
                if (response.isSuccessful) {
                    Log.i("SendMessageReceiver", "Wysłano wiadomość jako $sender do $recipient")
                } else {
                    Log.e("SendMessageReceiver", "Błąd API: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SendMessageReceiver", "Wyjątek: ${e.message}")
            }
        }
    }
}
