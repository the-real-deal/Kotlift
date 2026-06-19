package com.therealdeal.kotlift.ui.screens.workouts

sealed interface WorkoutsUiState {
    data object Loading : WorkoutsUiState
    data class Success(val message: String
    ) : WorkoutsUiState
    data class Error(val message: String) : WorkoutsUiState
}

sealed interface WorkoutsEvent {
    data object LoadData : WorkoutsEvent
    data class UpdateSearchText(val text: String) : WorkoutsEvent
    data class UpdateCategory(val category: String) : WorkoutsEvent
}
