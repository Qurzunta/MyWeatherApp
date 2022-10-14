package com.example.myweatherapp.ui.bookmarked

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myweatherapp.data.models.FavCity
import com.example.myweatherapp.repository.FavCityRepos
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookmarkedViewModel @Inject constructor(private val repos: FavCityRepos) : ViewModel() {

    val favCitieslist: List<FavCity> = repos.favcityist


    init {
        fetchData()
    }
    private fun fetchData() {
        viewModelScope.launch {
            try {
                repos.favcityist
                // here you have your CoursesFromDb
            } catch (e: Exception) {
                // handler error
            }
        }

    }
}