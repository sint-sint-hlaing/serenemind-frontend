package com.serenemind.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FocusViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FocusViewModel::class.java)) {
            return FocusViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
