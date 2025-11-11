package dev.klarkengkoy.triptrack.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * A data class to hold the state for the centralized TopAppBar.
 * It uses Composable lambdas to provide maximum flexibility for the UI.
 */
data class TopAppBarState(
    val title: @Composable () -> Unit = {},
    val navigationIcon: @Composable () -> Unit = {},
    val actions: @Composable RowScope.() -> Unit = {},
    val isCenterAligned: Boolean = false
)

data class MainUiState(
    val topAppBarState: TopAppBarState = TopAppBarState()
)

/**
 * ViewModel responsible for managing the state of shared UI components, like the TopAppBar.
 */
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    /**
     * Allows the current screen to configure the centralized TopAppBar.
     */
    fun setTopAppBarState(
        title: @Composable () -> Unit,
        navigationIcon: @Composable () -> Unit,
        actions: @Composable RowScope.() -> Unit,
        isCenterAligned: Boolean = false
    ) {
        _uiState.update {
            it.copy(
                topAppBarState = TopAppBarState(
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    isCenterAligned = isCenterAligned
                )
            )
        }
    }

    /**
     * Resets the TopAppBar to its default, empty state.
     * Typically called when a screen is disposed.
     */
    fun resetTopAppBarState() {
        _uiState.update { it.copy(topAppBarState = TopAppBarState()) }
    }
}
