package com.serenemind.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.UserProfileResponse
import com.serenemind.network.NetworkResult
import com.serenemind.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class EditProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Initial)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile.asStateFlow()

    fun fetchUserProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile().collect { result ->
                if (result is NetworkResult.Success) {
                    _userProfile.value = result.data
                }
            }
        }
    }

    fun updateProfile(
        context: Context,
        fullname: String,
        username: String,
        birthday: String,
        bio: String,
        avatarUri: Uri?
    ) {
        viewModelScope.launch {
            val avatarPart = avatarUri?.let { uri ->
                prepareFilePart(context, "avatar", uri)
            }
            
            userRepository.updateUserProfile(fullname, username, birthday, bio, avatarPart).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = EditProfileUiState.Loading
                    is NetworkResult.Success -> _uiState.value = EditProfileUiState.Success
                    is NetworkResult.Error -> _uiState.value = EditProfileUiState.Error(result.message)
                }
            }
        }
    }

    private fun prepareFilePart(context: Context, partName: String, fileUri: Uri): MultipartBody.Part? {
        val file = getFileFromUri(context, fileUri) ?: return null
        val requestFile = file.asRequestBody(
            context.contentResolver.getType(fileUri)?.toMediaTypeOrNull()
        )
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val file = File(context.cacheDir, "temp_avatar_${System.currentTimeMillis()}.jpg")
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }
}

sealed interface EditProfileUiState {
    object Initial : EditProfileUiState
    object Loading : EditProfileUiState
    object Success : EditProfileUiState
    data class Error(val message: String) : EditProfileUiState
}
