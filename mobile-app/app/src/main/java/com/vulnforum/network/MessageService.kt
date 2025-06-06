package com.vulnforum.network

import retrofit2.Call
import com.vulnforum.data.Message
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MessageService {
    @GET("api/messages/")
    fun getMessages(): Call<List<Message>>

    @POST("api/messages/")
    fun sendMessage(@Body message: Message): Call<Map<String, Any>>
}