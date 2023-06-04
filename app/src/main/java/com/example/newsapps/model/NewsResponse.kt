package com.example.newsapps.model

data class NewsResponse(
    val articles: List<BookmarkArticle>,
    val status: String,
    val totalResults: Int
)