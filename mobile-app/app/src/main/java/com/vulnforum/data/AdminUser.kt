package com.vulnforum.data

data class AdminUser(
    val id: Int,
    val username: String,
    val balance: Float,
    val unlocked_articles: List<Int>
)