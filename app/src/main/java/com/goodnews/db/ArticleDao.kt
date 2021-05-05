package com.example.goodnews.ui.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.goodnews.ui.retro.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artical:Article):Long

    @Query("SELECT * FROM articles")
    fun getAllArticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}