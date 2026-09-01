package com.calai.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calai.data.local.MealEntity
import com.calai.data.local.MealRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiaryViewModel(private val repository: MealRepository) : ViewModel() {
	val meals: StateFlow<List<MealEntity>> = repository.observeAllMeals()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	fun deleteMeal(meal: MealEntity) {
		viewModelScope.launch { repository.deleteMeal(meal) }
	}

	fun updateMeal(meal: MealEntity) {
		viewModelScope.launch { repository.updateMeal(meal) }
	}

	companion object {
		fun factory(repository: MealRepository): ViewModelProvider.Factory =
			object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel> create(modelClass: Class<T>): T =
					DiaryViewModel(repository) as T
			}
	}
}
