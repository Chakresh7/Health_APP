package com.calai.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calai.data.local.ChatRepository
import com.calai.data.local.ChatSessionEntity
import com.calai.data.local.MealRepository
import com.calai.data.local.UserPreferences
import com.calai.data.remote.ApiClient
import com.calai.data.remote.ChatRequest
import com.calai.data.remote.toUserMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
	val text: String,
	val fromUser: Boolean
)

data class ChatUiState(
	val sessions: List<ChatSessionEntity> = emptyList(),
	val activeSessionId: Long? = null,
	val messages: List<ChatMessage> = listOf(WelcomeMessage),
	val input: String = "",
	val isSending: Boolean = false,
	val error: String? = null,
	val showingHistory: Boolean = false
)

val WelcomeMessage = ChatMessage(
	text = "Ask about today's calories, remaining protein, or what to eat next. Replies are estimates, not medical advice.",
	fromUser = false
)

class ChatViewModel(
	private val mealRepository: MealRepository,
	private val preferences: UserPreferences,
	private val chatRepository: ChatRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(ChatUiState())
	val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
	private var messagesJob: Job? = null

	init {
		viewModelScope.launch {
			chatRepository.observeSessions().collect { sessions ->
				_uiState.update { it.copy(sessions = sessions) }
			}
		}
	}

	fun onInputChange(value: String) {
		_uiState.update { it.copy(input = value, error = null) }
	}

	fun showHistory(show: Boolean) {
		_uiState.update { it.copy(showingHistory = show) }
	}

	fun newChat() {
		messagesJob?.cancel()
		_uiState.update {
			it.copy(
				activeSessionId = null,
				messages = listOf(WelcomeMessage),
				input = "",
				error = null,
				isSending = false,
				showingHistory = false
			)
		}
	}

	fun openSession(sessionId: Long) {
		messagesJob?.cancel()
		_uiState.update { it.copy(activeSessionId = sessionId, showingHistory = false, error = null) }
		messagesJob = viewModelScope.launch {
			chatRepository.observeMessages(sessionId).collect { rows ->
				val mapped = rows.map { ChatMessage(it.text, it.fromUser) }
				_uiState.update {
					it.copy(messages = mapped.ifEmpty { listOf(WelcomeMessage) })
				}
			}
		}
	}

	fun deleteSession(sessionId: Long) {
		viewModelScope.launch {
			chatRepository.deleteSession(sessionId)
			if (_uiState.value.activeSessionId == sessionId) newChat()
		}
	}

	fun send(preset: String? = null) {
		val text = (preset ?: _uiState.value.input).trim()
		if (text.isBlank() || _uiState.value.isSending) return

		viewModelScope.launch {
			_uiState.update { state ->
				val visible = state.messages.filter { it != WelcomeMessage }
				state.copy(
					messages = visible + ChatMessage(text, true),
					input = if (preset == null) "" else state.input,
					isSending = true,
					error = null
				)
			}

			var sessionId = _uiState.value.activeSessionId
			if (sessionId == null) {
				sessionId = chatRepository.createSession(titleFrom(text))
				openSession(sessionId)
			} else {
				_uiState.value.sessions.find { it.id == sessionId }?.let { chatRepository.touchSession(it) }
			}
			chatRepository.addMessage(sessionId, text, true)

			val url = preferences.currentApiBaseUrl()
			runCatching {
				val meals = mealRepository.observeTodayMeals().first()
				val goals = preferences.goals.first()
				ApiClient.repository(url).chat(
					ChatRequest(
						message = text,
						calories_consumed = meals.sumOf { meal -> meal.calories },
						calorie_target = goals.calorieTarget,
						protein_g = meals.sumOf { meal -> meal.proteinG },
						carbs_g = meals.sumOf { meal -> meal.carbsG },
						fat_g = meals.sumOf { meal -> meal.fatG },
						protein_target = goals.proteinTarget.toDouble(),
						carbs_target = goals.carbsTarget.toDouble(),
						fat_target = goals.fatTarget.toDouble()
					)
				)
			}.fold(
				onSuccess = { response ->
					chatRepository.addMessage(sessionId, response.reply, false)
					_uiState.update { it.copy(isSending = false) }
				},
				onFailure = { error ->
					_uiState.update {
						it.copy(
							isSending = false,
							error = error.toUserMessage("Couldn't reach Ask Cal.ai.", url)
						)
					}
				}
			)
		}
	}

	private fun titleFrom(text: String): String {
		val cleaned = text.trim().replace('\n', ' ')
		return if (cleaned.length <= 36) cleaned else cleaned.take(36).trimEnd() + "…"
	}

	companion object {
		fun factory(
			mealRepository: MealRepository,
			preferences: UserPreferences,
			chatRepository: ChatRepository
		): ViewModelProvider.Factory =
			object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel> create(modelClass: Class<T>): T =
					ChatViewModel(mealRepository, preferences, chatRepository) as T
			}
	}
}
