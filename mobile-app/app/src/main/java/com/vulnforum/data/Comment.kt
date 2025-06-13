package com.vulnforum.data

import com.google.gson.annotations.SerializedName

data class Comment(
    val id: Int,
    val text: String,
    @SerializedName("file_path")
    val filePath: String? = null
)
