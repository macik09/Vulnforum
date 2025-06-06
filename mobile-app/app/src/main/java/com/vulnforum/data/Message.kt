package com.vulnforum.data

data class Message(
    val id: Int,
    val sender: String,
    val recipient: String,
    val content: String,
    val timestamp: String
)
