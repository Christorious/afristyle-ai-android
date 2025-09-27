package com.afristyle.ai.ui.screens.outfits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afristyle.ai.data.models.AfricanOutfit
import com.afristyle.ai.data.models.AfricanStyle
import com.afristyle.ai.data.repository.OutfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OutfitBrowserUiState(
    val isLoading: Boolean = false,
    val outfits: List<AfricanOutfit> = emptyList(),
    val selectedCategory: AfricanStyle? = null,
    val searchQuery: String = "",
    val error: String? = null
)

@HiltViewModel
class OutfitBrowserViewModel @Inject constructor(
    private val outfitRepository: OutfitRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OutfitBrowserUiState())
    val uiState: StateFlow<OutfitBrowserUiState> = _uiState.asStateFlow()
    
    init {
        loadOutfits()
    }
    
    private fun loadOutfits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val currentState = _uiState.value
                
                when {
                    currentState.searchQuery.isNotEmpty() -> {
                        outfitRepository.searchOutfits(currentState.searchQuery).collect { outfits ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                outfits = outfits,
                                error = null
                            )
                        }
                    }
                    
                    currentState.selectedCategory != null -> {
                        outfitRepository.getOutfitsByStyle(currentState.selectedCategory).collect { outfits ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                outfits = outfits,
                                error = null
                            )
                        }
                    }
                    
                    else -> {
                        outfitRepository.getAllOutfits().collect { outfits ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                outfits = outfits,
                                error = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load outfits"
                )
            }
        }
    }
    
    fun selectCategory(category: AfricanStyle?) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            searchQuery = "" // Clear search when selecting category
        )
        loadOutfits()
    }
    
    fun searchOutfits(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            selectedCategory = null // Clear category when searching
        )
        
        if (query.length >= 2 || query.isEmpty()) {
            loadOutfits()
        }
    }
}