package com.example.newsapps.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.newsapps.R
import com.example.newsapps.viewmodel.NewsViewModel
import com.example.newsapps.viewmodel.NewsViewModelProviderFactory
import com.example.newsapps.database.NewsDatabase
import com.example.newsapps.databinding.ActivityDetailNewsBinding
import com.example.newsapps.repository.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailNewsActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_URL = "extra_url"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_PUBLISHED = "extra_published"
    }

    private lateinit var binding: ActivityDetailNewsBinding
    private lateinit var viewModel: NewsViewModel
    private var checked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository = NewsRepository(NewsDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        val url = intent.getStringExtra(EXTRA_URL)
        val title = intent.getStringExtra(EXTRA_TITLE)
        val img = intent.getStringExtra(EXTRA_IMAGE)
        val published = intent.getStringExtra(EXTRA_PUBLISHED)

        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(url.toString())
        }

        CoroutineScope(Dispatchers.IO).launch {
            val count = viewModel.checkNews(title.toString())
            withContext(Dispatchers.Main) {
                checked = if (count.toInt() > 0) {
                    binding.btnBookmark.setImageDrawable(ContextCompat.getDrawable(this@DetailNewsActivity, R.drawable.baseline_bookmark_24))
                    true
                } else {
                    binding.btnBookmark.setImageDrawable(ContextCompat.getDrawable(this@DetailNewsActivity, R.drawable.baseline_bookmark_border_24))
                    false
                }
            }
        }

        binding.btnBookmark.setOnClickListener {
            checked = !checked
            if(checked) {
                Toast.makeText(this@DetailNewsActivity, getString(R.string.add_bookmark), Toast.LENGTH_LONG).show()
                binding.btnBookmark.setImageDrawable(ContextCompat.getDrawable(this@DetailNewsActivity, R.drawable.baseline_bookmark_24))
                viewModel.saveNews(url.toString(), title.toString(), img.toString(), published.toString())
            } else {
                Toast.makeText(this@DetailNewsActivity, getString(R.string.del_bookmark), Toast.LENGTH_LONG).show()
                binding.btnBookmark.setImageDrawable(ContextCompat.getDrawable(this@DetailNewsActivity, R.drawable.baseline_bookmark_border_24))
                viewModel.deleteNews(title.toString())
            }
        }

        supportActionBar?.title = "About This Article"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}