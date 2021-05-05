package com.example.goodnews.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodnews.R
import com.example.goodnews.ui.NewsActivity
import com.example.goodnews.ui.NewsViewModel
import com.example.goodnews.ui.adapters.NewsAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

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
                    R.id.action_savedNewsFragment_to_articleFragment3,
                    bundle
            )
        }


        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles)
        })

        val itemTouchHelperCallBack = initItemTouchHelperCallback(view)

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(fragmentSavedNewsRV)
        }

    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        fragmentSavedNewsRV.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun initItemTouchHelperCallback(view: View): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteAndUndoDelete(viewHolder, view)
            }

        }
    }

    private fun deleteAndUndoDelete(viewHolder: RecyclerView.ViewHolder, view: View) {
        val position = viewHolder.adapterPosition
        val article = newsAdapter.differ.currentList[position]
        viewModel.deleteArticle(article)
        Snackbar.make(
                view,
                getString(R.string.successfully_deleted_item),
                Snackbar.LENGTH_LONG
        ).apply {
            setAction("Undo") { viewModel.insertArticle(article) }
            show()
        }
    }
}
