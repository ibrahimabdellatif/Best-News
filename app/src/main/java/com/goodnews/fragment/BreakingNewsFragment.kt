package com.example.goodnews.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
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
import com.example.goodnews.ui.util.Constants.Companion.COUNTRY_CODE
import com.example.goodnews.ui.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.goodnews.ui.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    private val TAG = "BreakingNewsFragment"
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()

        newsAdapter.setOnArticleClickListener {
            it
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            findNavController().navigate(
                    R.id.action_breakingNewsFragment_to_articleFragment3,
                    bundle
            )
        }


        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> onSuccessResponse(response)
                is Resource.Error -> onErrorResponse(response)

                is Resource.Loading -> showProgressBar()
            }
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        fragmentBreakingNewsRV.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            // add our custom scrollListener
            addOnScrollListener(customScrollListener)
        }
    }

    private fun onSuccessResponse(response: Resource<newsResponse>) {
        // get data successfully
        hideProgressBar()
        response.data.let { newsResponse ->
            newsAdapter.differ.submitList(newsResponse?.articles)
            val totalResultsNumber = newsResponse?.totalResults ?: 0
            val totalPages = totalResultsNumber / QUERY_PAGE_SIZE + 2
            isLastPage = (viewModel.breakingNewsPage == totalPages)
            if (isLastPage) fragmentBreakingNewsRV.setPadding(0, 0, 0, 0)
        }
    }

    private fun onErrorResponse(response: Resource<newsResponse>) {
        hideProgressBar()
        response.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideProgressBar() {
        fragmentBreakingNewsPaginationPB.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        fragmentBreakingNewsPaginationPB.visibility = View.VISIBLE
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotTheLastPage && isAtLastItem &&
                    isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getBreakingNews(COUNTRY_CODE)
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