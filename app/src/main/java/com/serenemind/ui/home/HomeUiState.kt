package com.serenemind.ui.home

import com.serenemind.model.response.DashboardResponse

interface HomeUiState {
    object Loading: HomeUiState
    data class  Success (val data: DashboardResponse): HomeUiState
    data class Error (val message:String): HomeUiState
}