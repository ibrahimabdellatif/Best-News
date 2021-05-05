package com.example.goodnews.ui.retro

data class newsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)