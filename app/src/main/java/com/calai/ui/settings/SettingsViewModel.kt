package com.calai.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calai.data.local.UserGoals
import com.calai.data.local.UserPreferences
import com.calai.data.remote.ApiClient
import com.calai.data.remote.ApiHost
import com.calai.data.remote.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
	val calorieTarget: String = "2000",
	val proteinTarget: String = "100",
	val carbsTarget: String = "200",
	val fatTarget: String = "65",
	val apiBaseUrl: String = "",
	val statusMessage: String? = null,
	val saved: Boolean = false
)

class SettingsViewModel(
	private val preferences: UserPreferences
) : ViewModel() {
	private val _uiState = MutableStateFlow(SettingsUiState())
	val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			preferences.goals.collect { goals -> applyGoals(goals) }
		}
	}

	fun onCalorieChange(value: String) = _uiState.update { it.copy(calorieTarget = value, saved = false) }
	fun onProteinChange(value: String) = _uiState.update { it.copy(proteinTarget = value, saved = false) }
	fun onCarbsChange(value: String) = _uiState.update { it.copy(carbsTarget = value, saved = false) }
	fun onFatChange(value: String) = _uiState.update { it.copy(fatTarget = value, saved = false) }
	fun onApiUrlChange(value: String) = _uiState.update { it.copy(apiBaseUrl = value, saved = false) }

	fun save() {
		val state = _uiState.value
		viewModelScope.launch {
			preferences.saveGoals(
				calorieTarget = state.calorieTarget.toIntOrNull() ?: 2_000,
				proteinTarget = state.proteinTarget.toIntOrNull() ?: 100,
				carbsTarget = state.carbsTarget.toIntOrNull() ?: 200,
				fatTarget = state.fatTarget.toIntOrNull() ?: 65,
				apiBaseUrl = state.apiBaseUrl
			)
			_uiState.update { it.copy(saved = true, statusMessage = "Saved. Home and chat now use these targets.") }
		}
	}

	fun checkConnection() {
		viewModelScope.launch {
			val url = ApiHost.resolve(_uiState.value.apiBaseUrl)
			_uiState.update { it.copy(statusMessage = "Checking $url …") }
			runCatching { ApiClient.repository(url).health() }.fold(
				onSuccess = { health ->
					_uiState.update { it.copy(statusMessage = "Backend is ${health.status} at $url") }
				},
				onFailure = { error ->
					_uiState.update {
						it.copy(statusMessage = error.toUserMessage("Connection failed.", url))
					}
				}
			)
		}
	}

	private fun applyGoals(goals: UserGoals) {
		_uiState.update {
			it.copy(
				calorieTarget = goals.calorieTarget.toString(),
				proteinTarget = goals.proteinTarget.toString(),
				carbsTarget = goals.carbsTarget.toString(),
				fatTarget = goals.fatTarget.toString(),
				apiBaseUrl = goals.apiBaseUrl
			)
		}
	}

	companion object {
		fun factory(preferences: UserPreferences): ViewModelProvider.Factory =
			object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel> create(modelClass: Class<T>): T =
					SettingsViewModel(preferences) as T
			}
	}
}
