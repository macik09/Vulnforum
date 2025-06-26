package com.vulnforum.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


data class WalletBalanceResponse(
    val balance: Float
)

data class AddFundsRequest(
    val amount: Float,
    val nonce: String
)

data class PurchaseRequest(
    val amount: Float
)

data class PurchaseResponse(
    val message: String,
    val new_balance: Float
)


data class AddFundsResponse(
    val message: String,
    val new_balance: Float
)

interface WalletService {


    @GET("api/wallet")
    suspend fun getWalletBalance(): WalletBalanceResponse


    @POST("api/wallet/add")
    suspend fun addFunds(@Body request: AddFundsRequest): AddFundsResponse

    @POST("api/wallet/purchase")
    suspend fun purchase(@Body request: PurchaseRequest): Response<PurchaseResponse>
}