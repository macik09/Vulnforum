package com.vulnforum.data

import com.google.gson.annotations.SerializedName

data class Article(
    val id: Int,
    val title: String,
    val content: String,
    @SerializedName("is_paid") val isPaid: Boolean,
    @SerializedName("is_unlocked") val isUnlocked: Boolean
)
