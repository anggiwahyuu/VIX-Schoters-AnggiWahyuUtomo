package com.example.newsapps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapps.databinding.NewsItemRowBinding
import com.example.newsapps.model.BookmarkArticle

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<BookmarkArticle>() {
        override fun areItemsTheSame(oldItem: BookmarkArticle, newItem: BookmarkArticle): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: BookmarkArticle, newItem: BookmarkArticle): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = NewsItemRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = differ.currentList[position]
        holder.bind(news)
    }

    inner class NewsViewHolder(private val binding: NewsItemRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(news: BookmarkArticle) {
            binding.apply {
                publishedAt.text = news.publishedAt
                Glide.with(itemView)
                    .load(news.urlToImage)
                    .into(newsImage)
                newsHeadline.text = news.title
            }

            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(news)
            }
        }
    }

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: BookmarkArticle)
    }
}