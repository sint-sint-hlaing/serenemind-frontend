package com.serenemind.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.repository.CommunityRepository

class SavedPostsViewModelFactory(
    private val communityRepository: CommunityRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedPostsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavedPostsViewModel(communityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
