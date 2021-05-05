package com.example.goodnews.ui.api

import com.example.goodnews.ui.retro.newsResponse
import com.example.goodnews.ui.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode : String = "us" ,
        @Query("page")
        pageNumber:Int =1 ,
        @Query("apikey")
        apikey:String = API_KEY


    ):Response<newsResponse>

    //for search
    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery : String,
        @Query("page")
        pageNumber:Int =1 ,
        @Query("apikey")
        apikey:String = API_KEY


    ):Response<newsResponse>
}