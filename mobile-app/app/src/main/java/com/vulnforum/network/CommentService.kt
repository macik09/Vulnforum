package com.vulnforum.network


import com.vulnforum.data.Comment
import java.lang.Void
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CommentService {

    @GET("/api/articles/{id}/comments")
    fun getCommentsForArticle(@Path("id") articleId: Int): Call<List<Comment>>

    @Multipart
    @POST("/api/articles/{id}/comments")
    fun postComment(
        @Path("id") articleId: Int,
        @Part("text") text: RequestBody,
        @Part file: MultipartBody.Part? = null
    ): Call<Void>
}
