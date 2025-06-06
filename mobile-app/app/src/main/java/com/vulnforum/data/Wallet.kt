package com.vulnforum.data

data class Wallet(
    var balance: Int = 0, // ilość vulndolców
    val unlockedArticleIds: MutableSet<Int> = mutableSetOf()
)
