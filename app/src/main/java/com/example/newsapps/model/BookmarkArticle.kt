package com.example.newsapps.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(
    tableName = "articles"
)

data class BookmarkArticle(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val publishedAt: String? = null,
    val title: String? = null,
    val url: String? = null,
    val urlToImage: String? = null
): Serializable
