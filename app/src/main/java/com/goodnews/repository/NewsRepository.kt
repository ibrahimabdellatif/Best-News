package com.example.goodnews.ui.repository

import androidx.lifecycle.ViewModel
import com.example.goodnews.ui.api.RetrofitInstance
import com.example.goodnews.ui.db.ArticleDatabase
import com.example.goodnews.ui.retro.Article

class NewsRepository(val db: ArticleDatabase) : ViewModel() {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
            RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)


    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
            RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun insertArticle(article: Article) = db.getArticleDao().insert(article)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()
}