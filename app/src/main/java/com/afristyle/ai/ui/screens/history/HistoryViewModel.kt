package com.afristyle.ai.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afristyle.ai.data.models.TryOnResult
import com.afristyle.ai.data.repository.TryOnHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = false,
    val tryOnResults: List<TryOnResult> = emptyList(),
    val filteredResults: List<TryOnResult> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val tryOnHistoryRepository: TryOnHistoryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    private var allResults: List<TryOnResult> = emptyList()
    
    init {
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                tryOnHistoryRepository.getAllTryOnResults().collect { results ->
                    allResults = results
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tryOnResults = results,
                        filteredResults = results,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load history"
                )
            }
        }
    }
    
    fun searchHistory(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        val filteredResults = if (query.isEmpty()) {
            allResults
        } else {
            allResults.filter { result ->
                // In a real app, you might search by outfit name, date, etc.
                // For now, we'll just filter by result ID or notes
                result.id.toString().contains(query, ignoreCase = true) ||
                result.notes?.contains(query, ignoreCase = true) == true
            }
        }
        
        _uiState.value = _uiState.value.copy(
            tryOnResults = filteredResults,
            filteredResults = filteredResults
        )
    }
    
    fun deleteResult(resultId: Long) {
        viewModelScope.launch {
            try {
                tryOnHistoryRepository.deleteTryOnResult(resultId)
                // The flow will automatically update the UI
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete result: ${e.message}"
                )
            }
        }
    }
    
    fun clearAllHistory() {
        viewModelScope.launch {
            try {
                tryOnHistoryRepository.deleteAllTryOnResults()
                // The flow will automatically update the UI
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to clear history: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}