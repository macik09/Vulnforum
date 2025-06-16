package com.vulnforum.ui.wallet

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.vulnforum.network.AddFundsRequest
import com.vulnforum.network.ApiClient
import com.vulnforum.network.WalletService
import com.vulnforum.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeeplinkAddFundsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this
        val sessionManager = SessionManager(context)
        val token = sessionManager.getToken()

        val amountParam = intent?.data?.getQueryParameter("amount")
        val amount = amountParam?.toFloatOrNull()

        if (amount != null && amount > 0 && token != null) {
            val walletService = ApiClient.getClient(context).create(WalletService::class.java)
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                try {
                    val response = walletService.addFunds(AddFundsRequest(amount))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Dodano $amount vulndolców!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Błąd API: ${e.message}", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Nieprawidłowy deeplink lub brak tokena", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}