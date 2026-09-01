package com.calai.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calai.data.local.MealEntity
import com.calai.data.local.MealRepository
import com.calai.data.local.UserGoals
import com.calai.data.local.UserPreferences
import com.calai.data.remote.NutritionAnalysis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppNotification(
	val id: String,
	val title: String,
	val body: String,
	val timeLabel: String
)

data class HomeUiState(
	val meals: List<MealEntity> = emptyList(),
	val calories: Int = 0,
	val proteinG: Double = 0.0,
	val carbsG: Double = 0.0,
	val fatG: Double = 0.0,
	val calorieTarget: Int = 2_000,
	val proteinTarget: Int = 100,
	val carbsTarget: Int = 200,
	val fatTarget: Int = 65,
	val caloriesOverTarget: Int = 0,
	val insight: String = "Scan or describe your first meal to start today's log.",
	val selectedDayStart: Long = MealRepository.startOfDayMillis(System.currentTimeMillis()),
	val isToday: Boolean = true,
	val displayName: String = "",
	val notifications: List<AppNotification> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
	private val repository: MealRepository,
	private val preferences: UserPreferences
) : ViewModel() {
	private val selectedDayStart = MutableStateFlow(
		MealRepository.startOfDayMillis(System.currentTimeMillis())
	)

	val uiState: StateFlow<HomeUiState> = combine(
		selectedDayStart.flatMapLatest { start ->
			repository.observeMealsForDay(start, start + MealRepository.DAY_MS)
		},
		preferences.goals,
		selectedDayStart
	) { meals, goals, dayStart ->
		val calories = meals.sumOf { it.calories }
		val protein = meals.sumOf { it.proteinG }
		val carbs = meals.sumOf { it.carbsG }
		val fat = meals.sumOf { it.fatG }
		val today = MealRepository.startOfDayMillis(System.currentTimeMillis())
		HomeUiState(
			meals = meals,
			calories = calories,
			proteinG = protein,
			carbsG = carbs,
			fatG = fat,
			calorieTarget = goals.calorieTarget,
			proteinTarget = goals.proteinTarget,
			carbsTarget = goals.carbsTarget,
			fatTarget = goals.fatTarget,
			caloriesOverTarget = (calories - goals.calorieTarget).coerceAtLeast(0),
			insight = dailyInsight(meals, calories, protein, goals, dayStart == today),
			selectedDayStart = dayStart,
			isToday = dayStart == today,
			displayName = goals.displayName,
			notifications = buildNotifications(meals, calories, protein, goals, dayStart == today)
		)
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

	fun selectDay(dayStartMillis: Long) {
		selectedDayStart.value = MealRepository.startOfDayMillis(dayStartMillis)
	}

	fun saveAnalysis(analysis: NutritionAnalysis, mealType: String, imageUri: String? = null) {
		viewModelScope.launch {
			repository.insertMeal(
				MealEntity(
					foodName = analysis.food_name,
					calories = analysis.estimated_calories,
					proteinG = analysis.protein_g,
					carbsG = analysis.carbs_g,
					fatG = analysis.fat_g,
					mealType = mealType,
					imageUri = imageUri
				)
			)
			selectDay(System.currentTimeMillis())
		}
	}

	fun updateMeal(meal: MealEntity) {
		viewModelScope.launch { repository.updateMeal(meal) }
	}

	companion object {
		fun factory(
			repository: MealRepository,
			preferences: UserPreferences
		): ViewModelProvider.Factory =
			object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel> create(modelClass: Class<T>): T =
					HomeViewModel(repository, preferences) as T
			}
	}
}

private fun dailyInsight(
	meals: List<MealEntity>,
	calories: Int,
	protein: Double,
	goals: UserGoals,
	isToday: Boolean
): String {
	if (meals.isEmpty()) {
		return if (isToday) {
			"Scan or describe your first meal to start today's log."
		} else {
			"No meals logged on this day."
		}
	}
	if (calories > goals.calorieTarget) {
		val over = calories - goals.calorieTarget
		return "This day is $over kcal over the ${goals.calorieTarget} kcal target. Estimates only."
	}
	if (protein < goals.proteinTarget * 0.5) {
		return "Protein is still below target for this day."
	}
	val remaining = (goals.calorieTarget - calories).coerceAtLeast(0)
	return "This day has $remaining kcal remaining against your target."
}

private fun buildNotifications(
	meals: List<MealEntity>,
	calories: Int,
	protein: Double,
	goals: UserGoals,
	isToday: Boolean
): List<AppNotification> {
	if (!isToday) return emptyList()
	val items = mutableListOf<AppNotification>()
	if (meals.isEmpty()) {
		items += AppNotification("log", "Log your first meal", "Scan or describe what you ate to start today’s totals.", "Today")
	} else {
		items += AppNotification("saved", "${meals.size} meal${if (meals.size == 1) "" else "s"} saved", "Your diary is up to date for today.", "Today")
	}
	val remaining = (goals.calorieTarget - calories).coerceAtLeast(0)
	if (calories > 0 && remaining > 0) {
		items += AppNotification("kcal", "$remaining kcal left", "You still have room before your ${goals.calorieTarget} kcal target.", "Today")
	}
	if (meals.isNotEmpty() && protein < goals.proteinTarget * 0.5) {
		items += AppNotification("protein", "Protein is behind", "You’re at ${protein.toInt()}g of ${goals.proteinTarget}g. A protein-rich meal can help.", "Today")
	}
	return items
}
