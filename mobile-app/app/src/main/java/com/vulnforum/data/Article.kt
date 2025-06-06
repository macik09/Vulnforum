package com.vulnforum.data

data class Article(
    val id: Int,
    val title: String,
    val content: String,
    val isPaid: Boolean,
    var isUnlocked: Boolean = false // domyślnie zablokowany jeśli płatny
)
val dummyArticles = listOf(
    Article(
        id = 1,
        title = "Darmowy artykuł o bezpieczeństwie",
        content = "To jest darmowa zawartość artykułu...",
        isPaid = false
    ),
    Article(
        id = 2,
        title = "Płatny artykuł - zaawansowane techniki",
        content = "Tutaj znajdziesz treść dostępną tylko po zakupie...",
        isPaid = true
    ),
    Article(
        id = 3,
        title = "Inny darmowy artykuł",
        content = "Treść darmowego artykułu nr 3",
        isPaid = false
    )
)