package com.serenemind.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.repository.CommunityRepository

class CommunityViewModelFactory(
    private val communityRepository: CommunityRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommunityViewModel(communityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
