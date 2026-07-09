package com.serenemind.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.model.response.PostResponse
import com.serenemind.repository.CommunityRepository

class PostDetailViewModelFactory(
    private val communityRepository: CommunityRepository,
    private val postId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostDetailViewModel(communityRepository, postId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
