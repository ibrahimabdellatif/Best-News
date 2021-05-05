package com.example.goodnews.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodnews.R
import com.example.goodnews.ui.NewsActivity
import com.example.goodnews.ui.NewsViewModel
import com.example.goodnews.ui.adapters.NewsAdapter
import com.example.goodnews.ui.retro.newsResponse
import com.example.goodnews.ui.util.Constants
import com.example.goodnews.ui.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.goodnews.ui.util.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    private val TAG = "SearchNewsFragment"
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()



        newsAdapter.setOnArticleClickListener {
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            findNavController().navigate(
                    R.id.action_searchNewsFragment_to_articleFragment3,
                    bundle
            )
        }


        var job: Job? = null
        fragmentSearchNewsET.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                if (editable.toString().isNotEmpty()) {
                    viewModel.searchNewsPage = 1
                    viewModel.searchNewsResponse = null
                    viewModel.searchNews(editable.toString())
                }
            }
        }
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> onSuccessResponse(response)
                is Resource.Error -> onErrorResponse(response)
                is Resource.Loading -> showProgressBar()
            }
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        fragmentSearchNewsRV.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(customScrollListener)
        }
    }

    private fun onSuccessResponse(response: Resource<newsResponse>) {
        hideProgressBar()
        response.data.let { newsResponse ->
            newsAdapter.differ.submitList(newsResponse?.articles?.toList())

        }
    }

    private fun onErrorResponse(response: Resource<newsResponse>) {
        hideProgressBar()
        response.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideProgressBar() {
        fragmentSearchNewsPaginationPB.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        fragmentSearchNewsPaginationPB.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val customScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount


            val isNotLoadingAndNotTheLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisiblePosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisiblePosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotTheLastPage && isAtLastItem &&
                    isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.searchNews(fragmentSearchNewsET.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }
    }
}