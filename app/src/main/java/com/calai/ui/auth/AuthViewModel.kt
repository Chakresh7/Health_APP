package com.calai.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calai.data.auth.AuthRepository
import com.calai.data.auth.AuthUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
	private val authRepository: AuthRepository
) : ViewModel() {
	val uiState: StateFlow<AuthUiState> = authRepository.uiState

	fun signInWithGoogle(activity: Activity, onSuccess: () -> Unit) {
		viewModelScope.launch {
			if (authRepository.signInWithGoogle(activity)) {
				onSuccess()
			}
		}
	}

	fun signOut(onDone: () -> Unit) {
		viewModelScope.launch {
			authRepository.signOut()
			onDone()
		}
	}

	fun clearError() = authRepository.clearError()

	companion object {
		fun factory(authRepository: AuthRepository): ViewModelProvider.Factory =
			object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel> create(modelClass: Class<T>): T =
					AuthViewModel(authRepository) as T
			}
	}
}
