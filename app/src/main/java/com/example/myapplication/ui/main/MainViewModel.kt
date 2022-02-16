package com.example.myapplication.ui.main

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.myapplication.network.ApiInterface
import com.example.myapplication.repository.FlickerRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _currentQuery = MutableLiveData("")
    private val apiInterface = ApiInterface.create()
    private val repository: FlickerRepository = FlickerRepository(apiInterface)
    val mainPhotos = _currentQuery.switchMap { query ->
        repository.getSearchResultStream(query).cachedIn(viewModelScope).asLiveData()
    }

    fun searchPhotos(key: String) = viewModelScope.launch {
        _currentQuery.value = key
    }
}