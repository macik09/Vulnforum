package com.vulnforum.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor


object ApiClient {
    private var retrofit: Retrofit? = null


    fun getClient(baseUrl: String = "http://10.0.2.2:5000/"): Retrofit {
        if (retrofit == null) {
            // Tworzymy interceptor logujący body i nagłówki
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            // Dodajemy interceptor do klienta OkHttp
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}