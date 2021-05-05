package com.example.goodnews.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodnews.ui.repository.NewsRepository
import com.example.goodnews.ui.retro.Article
import com.example.goodnews.ui.retro.newsResponse
import com.example.goodnews.ui.util.Constants.Companion.COUNTRY_CODE
import com.example.goodnews.ui.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class NewsViewModel(val newsRepository: NewsRepository) : ViewModel() {

    var breakingNewsResponse: newsResponse? = null
    var searchNewsResponse: newsResponse? = null

    val breakingNews: MutableLiveData<Resource<newsResponse>> = MutableLiveData()

    var breakingNewsPage = 1

    val searchNews: MutableLiveData<Resource<newsResponse>> = MutableLiveData()
    var searchNewsPage = 1

    init {
        getBreakingNews(COUNTRY_CODE)
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        try {
            val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
            breakingNews.postValue(handelBreakingNewsResponse(response))
        } catch (e: Exception) {
            breakingNews.postValue(Resource.Error("No internet connection"))
        }
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        try {
            val response = newsRepository.searchNews(searchQuery, searchNewsPage)
            searchNews.postValue(handelSearchNewsResponse(response))
        } catch (e: Exception) {
            searchNews.postValue(Resource.Error("No internet connection"))
        }
    }

    private fun handelBreakingNewsResponse(response: Response<newsResponse>): Resource<newsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) breakingNewsResponse = resultResponse
                else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    private fun handelSearchNewsResponse(response: Response<newsResponse>): Resource<newsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) searchNewsResponse = resultResponse
                else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun insertArticle(article: Article) = viewModelScope.launch {
        newsRepository.insertArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()
}