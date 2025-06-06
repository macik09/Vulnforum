package com.vulnforum.data

data class Wallet(
    var balance: Float = 0f, // ilość vulndolców
    val unlockedArticleIds: MutableSet<Int> = mutableSetOf()
)
