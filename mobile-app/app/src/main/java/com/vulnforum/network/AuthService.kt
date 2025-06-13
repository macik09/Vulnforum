package com.vulnforum.network

import com.vulnforum.data.LoginRequest
import com.vulnforum.data.LoginResponse
import com.vulnforum.data.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>


    @POST("/api/register")
    fun register(@Body request: RegisterRequest): Call<Void>
}
