package com.therealdeal.kotlift.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.ThemeRepository
import com.therealdeal.kotlift.model.Theme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ThemeState(
    val theme: Theme
)

class ThemeViewModel(
    private val repository: ThemeRepository
) : ViewModel() {

    val state = repository.theme
        .map { theme -> ThemeState(theme) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ThemeState(Theme.System)
        )

    fun setTheme(theme: Theme){
        viewModelScope.launch {
            repository.setTheme(theme)
        }
    }
}