package com.vulnforum.network


import com.vulnforum.data.Article
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ArticleService {
    @GET("api/articles/")
    suspend fun getArticles(): List<Article>

    @POST("api/articles/unlock/{articleId}")
   fun unlockArticle(@Path("articleId") articleId: Int): Call<Map<String, Any>>
}
