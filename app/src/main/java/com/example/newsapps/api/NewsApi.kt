package com.example.newsapps.api

import com.example.newsapps.model.NewsResponse
import com.example.newsapps.util.Constant.Companion.apikey
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun getAllNews(
        @Query("country")
        domains: String = "us",

        @Query("category")
        category: String = "business",

        @Query("apiKey")
        apiKey: String = apikey
    ): Response<NewsResponse>
}