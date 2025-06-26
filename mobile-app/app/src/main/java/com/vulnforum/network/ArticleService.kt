package com.vulnforum.network


import com.vulnforum.data.Article
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ArticleService {
    @GET("api/articles/")
    suspend fun getArticles(): List<Article>

    @POST("api/articles/unlock/{articleId}")
    suspend fun unlockArticle(@Path("articleId") articleId: Int,@Header("X-Access-Key") xAccessKey: String ): Response<Map<String, Any>>


}
