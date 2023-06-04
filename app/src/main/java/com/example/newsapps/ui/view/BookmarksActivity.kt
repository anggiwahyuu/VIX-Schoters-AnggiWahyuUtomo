package com.example.newsapps.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapps.adapter.NewsAdapter
import com.example.newsapps.viewmodel.NewsViewModel
import com.example.newsapps.viewmodel.NewsViewModelProviderFactory
import com.example.newsapps.database.NewsDatabase
import com.example.newsapps.databinding.ActivityBookmarksBinding
import com.example.newsapps.model.BookmarkArticle
import com.example.newsapps.repository.NewsRepository

class BookmarksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarksBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository = NewsRepository(NewsDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        showRecyclerView()

        newsAdapter.setOnItemClickCallback(object: NewsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: BookmarkArticle) {
                Intent(this@BookmarksActivity, DetailNewsActivity::class.java).also {
                    it.putExtra(DetailNewsActivity.EXTRA_URL, data.url)
                    it.putExtra(DetailNewsActivity.EXTRA_TITLE, data.title)
                    startActivity(it)
                }
            }
        })

        viewModel.getSavedNews().observe(this) { article ->
            newsAdapter.differ.submitList(article)
        }

        supportActionBar?.title = "Bookmarks"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.viewNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@BookmarksActivity)
        }
    }
}