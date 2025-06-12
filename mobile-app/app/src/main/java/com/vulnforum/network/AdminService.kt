package com.vulnforum.network

import com.vulnforum.data.AdminUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

data class UpdateArticlePaymentStatusRequest(
    val is_paid: Boolean
)

interface AdminService {
    @GET("api/admin/users")
    suspend fun getAllUsers(): List<AdminUser>

    @PATCH("api/admin/articles/{articleId}")
    suspend fun updateArticlePaymentStatus(
        @Path("articleId") articleId: Int,
        @Body status: UpdateArticlePaymentStatusRequest
    ): Response<Unit>
}