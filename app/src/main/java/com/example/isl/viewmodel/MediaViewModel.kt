package com.example.isl.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isl.data.MediaItem
import com.example.isl.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MediaViewModel(
    private val repository: MediaRepository = MediaRepository()
) : ViewModel() {

    private val _imageItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val imageItems: StateFlow<List<MediaItem>> = _imageItems

    private val _videoItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val videoItems: StateFlow<List<MediaItem>> = _videoItems

    init {
        fetchMediaItems()
    }

    private fun fetchMediaItems() {
        viewModelScope.launch {
            try {
                val allItems = repository.getMediaItems()

                val images = allItems.filter { it.mediaType.name == "IMAGE" }
                val videos = allItems.filter { it.mediaType.name == "VIDEO" }

                Log.d("MediaViewModel", "Total items fetched: ${allItems.size}")
                Log.d("MediaViewModel", "Images: ${images.size}, Videos: ${videos.size}")

                _imageItems.value = images
                _videoItems.value = videos
            } catch (e: Exception) {
                Log.e("MediaViewModel", "Error fetching media items", e)
            }
        }
    }
}
