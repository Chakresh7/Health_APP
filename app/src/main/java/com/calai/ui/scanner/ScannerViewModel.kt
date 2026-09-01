package com.calai.ui.scanner

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calai.data.local.UserPreferences
import com.calai.data.remote.ApiClient
import com.calai.data.remote.NutritionAnalysis
import com.calai.data.remote.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ScannerUiState {
	data object Ready : ScannerUiState
	data object Analyzing : ScannerUiState
	data class Success(val analysis: NutritionAnalysis, val imageUri: Uri?) : ScannerUiState
	data class Error(val message: String) : ScannerUiState
}

class ScannerViewModel(
	private val preferences: UserPreferences
) : ViewModel() {
	private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Ready)
	val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

	fun analyzeImage(contentResolver: ContentResolver, imageUri: Uri) {
		viewModelScope.launch {
			_uiState.value = ScannerUiState.Analyzing
			val url = preferences.currentApiBaseUrl()
			_uiState.value = runCatching {
				ApiClient.repository(url).analyzeFood(contentResolver, imageUri)
			}.fold(
				onSuccess = { ScannerUiState.Success(it, imageUri) },
				onFailure = {
					Log.e("ScannerViewModel", "Food analysis failed", it)
					ScannerUiState.Error(it.toUserMessage("Analysis failed.", url))
				}
			)
		}
	}

	fun analyzeText(description: String) {
		val trimmed = description.trim()
		if (trimmed.isBlank()) {
			_uiState.value = ScannerUiState.Error("Describe what you ate, then try again.")
			return
		}
		viewModelScope.launch {
			_uiState.value = ScannerUiState.Analyzing
			val url = preferences.currentApiBaseUrl()
			_uiState.value = runCatching {
				ApiClient.repository(url).analyzeFoodText(trimmed)
			}.fold(
				onSuccess = { ScannerUiState.Success(it, null) },
				onFailure = {
					Log.e("ScannerViewModel", "Text analysis failed", it)
					ScannerUiState.Error(it.toUserMessage("Analysis failed.", url))
				}
			)
		}
	}

	fun reset() {
		_uiState.value = ScannerUiState.Ready
	}

	companion object {
		fun factory(preferences: UserPreferences): ViewModelProvider.Factory =
			object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel> create(modelClass: Class<T>): T =
					ScannerViewModel(preferences) as T
			}
	}
}
