package com.example.goodnews.ui.fragment

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.goodnews.R
import com.example.goodnews.ui.NewsActivity
import com.example.goodnews.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {


    lateinit var viewModel: NewsViewModel

    // use navigation component
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        val article = args.article

        fragmentArticleWebView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        fragmentArticleFab.setOnClickListener {

            viewModel.insertArticle(article)
            Snackbar.make(
                    view,
                    getString(R.string.article_saved_successfully),
                    Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}
