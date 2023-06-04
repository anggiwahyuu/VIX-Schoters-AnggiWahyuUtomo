package com.example.newsapps.repository

import com.example.newsapps.api.ApiRetrofit
import com.example.newsapps.database.NewsDatabase
import com.example.newsapps.model.BookmarkArticle

class NewsRepository(private val db: NewsDatabase) {
    suspend fun getNews(country: String, category: String) = ApiRetrofit.api.getAllNews(country, category)

    suspend fun insertNews(article: BookmarkArticle) = db.getNewsDao().insertNews(article)

    fun getSavedNews() = db.getNewsDao().getAllNews()

    suspend fun deleteNews(title: String) = db.getNewsDao().deleteNews(title)

    suspend fun checkNews(title: String) = db.getNewsDao().checkUser(title)
}