package com.vulnforum.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Model odpowiedzi dla pobierania salda
data class WalletBalanceResponse(
    val balance: Float
)

// Model żądania dla dodawania środków
data class AddFundsRequest(
    val amount: Float
)

// Model odpowiedzi dla dodawania środków (może być taki sam jak WalletBalanceResponse lub zawierać dodatkowe info)
data class AddFundsResponse(
    val message: String,
    val new_balance: Float // Odpowiada 'new_balance' z backendu
)

interface WalletService {

    /**
     * Pobiera aktualne saldo portfela zalogowanego użytkownika.
     * Endpoint na backendzie: GET /api/wallet
     */
    @GET("api/wallet")
    suspend fun getWalletBalance(): WalletBalanceResponse

    /**
     * Dodaje określoną kwotę do portfela zalogowanego użytkownika.
     * Endpoint na backendzie: POST /api/wallet/add
     * Oczekuje obiektu JSON z kluczem 'amount', np. {"amount": 5.0}
     *
     * @param request Obiekt AddFundsRequest zawierający kwotę do dodania.
     */
    @POST("api/wallet/add")
    suspend fun addFunds(@Body request: AddFundsRequest): AddFundsResponse
}