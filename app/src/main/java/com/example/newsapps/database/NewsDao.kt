package com.example.newsapps.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapps.model.BookmarkArticle

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(article: BookmarkArticle): Long

    @Query("Select * FROM articles")
    fun getAllNews(): LiveData<List<BookmarkArticle>>

    @Query("SELECT count(*) FROM articles WHERE articles.title = :title")
    suspend fun checkUser(title: String): String

    @Query("DELETE FROM articles WHERE articles.title = :title")
    suspend fun deleteNews(title: String)
}