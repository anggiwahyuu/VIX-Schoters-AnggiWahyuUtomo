package com.example.newsapps.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapps.R
import com.example.newsapps.adapter.NewsAdapter
import com.example.newsapps.viewmodel.NewsViewModel
import com.example.newsapps.viewmodel.NewsViewModelProviderFactory
import com.example.newsapps.database.NewsDatabase
import com.example.newsapps.databinding.ActivityMainBinding
import com.example.newsapps.model.BookmarkArticle
import com.example.newsapps.repository.NewsRepository
import com.example.newsapps.util.Resource

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository = NewsRepository(NewsDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        showRecyclerView()

        newsAdapter.setOnItemClickCallback(object: NewsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: BookmarkArticle) {
                Intent(this@MainActivity, DetailNewsActivity::class.java).also {
                    it.putExtra(DetailNewsActivity.EXTRA_URL, data.url)
                    it.putExtra(DetailNewsActivity.EXTRA_TITLE, data.title)
                    it.putExtra(DetailNewsActivity.EXTRA_IMAGE, data.urlToImage)
                    it.putExtra(DetailNewsActivity.EXTRA_PUBLISHED, data.publishedAt)
                    startActivity(it)
                }
            }
        })

        viewModel.breakingNews.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideLoadingBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }

                is Resource.Error -> {
                    hideLoadingBar()
                    response.message?.let { message ->
                        Toast.makeText(this, "An error occurred: $message", Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading -> {
                    showLoadingBar()
                }
            }
        }

        supportActionBar?.title = "News Apps"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMode(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setMode(selectedMenu: Int) {
        when (selectedMenu) {
            R.id.bookmarks -> {
                val intentBookmarks = Intent(this@MainActivity, BookmarksActivity::class.java)
                startActivity(intentBookmarks)
            }

            R.id.my_profile -> {
                val intentProfile = Intent(this@MainActivity, MyProfileActivity::class.java)
                startActivity(intentProfile)
            }
        }
    }

    private fun showLoadingBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.viewNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }
}