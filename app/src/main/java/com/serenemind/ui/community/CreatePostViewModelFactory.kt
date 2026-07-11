package com.serenemind.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.repository.CommunityRepository

class CreatePostViewModelFactory(
    private val communityRepository: CommunityRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreatePostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreatePostViewModel(communityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
