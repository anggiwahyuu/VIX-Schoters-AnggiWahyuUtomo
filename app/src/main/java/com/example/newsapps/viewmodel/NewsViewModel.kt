package com.example.newsapps.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapps.NewsApps
import com.example.newsapps.model.BookmarkArticle
import com.example.newsapps.model.NewsResponse
import com.example.newsapps.repository.NewsRepository
import com.example.newsapps.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

@Suppress("DEPRECATION")
class NewsViewModel(app: Application, private val newsRepository: NewsRepository) : AndroidViewModel(app) {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    init {
        getNews("us", "business")
    }

    private fun getNews(country: String, category: String) = viewModelScope.launch {
        getSafeNews(country, category)
    }

    private fun handleNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveNews(url: String, title: String, img: String, published: String) = viewModelScope.launch {
        val article = BookmarkArticle(
            null,
            published,
            title,
            url,
            img
        )
        newsRepository.insertNews(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteNews(title: String) = viewModelScope.launch {
        newsRepository.deleteNews(title)
    }

    suspend fun checkNews(title: String) = newsRepository.checkNews(title)

    private suspend fun getSafeNews(country: String, category: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if(internetConnection()) {
                val response = newsRepository.getNews(country, category)
                breakingNews.postValue(handleNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun internetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApps>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else{
            connectivityManager.activeNetworkInfo?.run{
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}