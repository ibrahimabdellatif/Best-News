package com.example.goodnews.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goodnews.R
import com.example.goodnews.ui.retro.Article
import kotlinx.android.synthetic.main.item_article.view.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // anonymous class
    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int {

        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentArticle = differ.currentList.get(position)

        holder.itemView.apply {
            // this is the view not the adapter
            Glide.with(this).load(currentArticle.urlToImage).into(itemArticleImgImageView)
            itemArticleSourceTV.text = currentArticle.source?.name ?: "unknown"
            itemArticleTitleTV.text = currentArticle.title
            itemArticleDescriptionTV.text = currentArticle.description
            itemArticlePublishedAtTV.text = currentArticle.publishedAt
            setOnClickListener { onArticleClickListener?.let { it(currentArticle) } }
        }
    }

    private var onArticleClickListener: ((Article) -> Unit)? = null


    fun setOnArticleClickListener(listener: (Article) -> Unit) {
        onArticleClickListener = listener
    }
}