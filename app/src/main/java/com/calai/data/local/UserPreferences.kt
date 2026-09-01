package com.calai.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.calai.data.remote.ApiHost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore("cal_ai_settings")

data class UserGoals(
	val calorieTarget: Int = 2_000,
	val proteinTarget: Int = 100,
	val carbsTarget: Int = 200,
	val fatTarget: Int = 65,
	val apiBaseUrl: String = ApiHost.resolve(null),
	val displayName: String = "",
	val goalType: String = "maintain",
	val onboardingComplete: Boolean = false
)

class UserPreferences(context: Context) {
	private val dataStore = context.applicationContext.settingsDataStore

	val goals: Flow<UserGoals> = dataStore.data.map { prefs ->
		UserGoals(
			calorieTarget = prefs[CALORIE_TARGET] ?: 2_000,
			proteinTarget = prefs[PROTEIN_TARGET] ?: 100,
			carbsTarget = prefs[CARBS_TARGET] ?: 200,
			fatTarget = prefs[FAT_TARGET] ?: 65,
			apiBaseUrl = ApiHost.resolve(prefs[API_BASE_URL]),
			displayName = prefs[DISPLAY_NAME].orEmpty(),
			goalType = prefs[GOAL_TYPE] ?: "maintain",
			onboardingComplete = prefs[ONBOARDING_COMPLETE] ?: false
		)
	}

	suspend fun currentApiBaseUrl(): String = goals.first().apiBaseUrl

	suspend fun saveGoals(
		calorieTarget: Int,
		proteinTarget: Int,
		carbsTarget: Int,
		fatTarget: Int,
		apiBaseUrl: String
	) {
		dataStore.edit { prefs ->
			prefs[CALORIE_TARGET] = calorieTarget.coerceIn(800, 10_000)
			prefs[PROTEIN_TARGET] = proteinTarget.coerceIn(10, 500)
			prefs[CARBS_TARGET] = carbsTarget.coerceIn(20, 800)
			prefs[FAT_TARGET] = fatTarget.coerceIn(10, 300)
			prefs[API_BASE_URL] = ApiHost.resolve(apiBaseUrl)
		}
	}

	suspend fun completeOnboarding(
		displayName: String,
		goalType: String,
		calorieTarget: Int,
		proteinTarget: Int,
		carbsTarget: Int,
		fatTarget: Int
	) {
		dataStore.edit { prefs ->
			prefs[DISPLAY_NAME] = displayName.trim()
			prefs[GOAL_TYPE] = goalType
			prefs[CALORIE_TARGET] = calorieTarget
			prefs[PROTEIN_TARGET] = proteinTarget
			prefs[CARBS_TARGET] = carbsTarget
			prefs[FAT_TARGET] = fatTarget
			prefs[ONBOARDING_COMPLETE] = true
		}
	}

	companion object {
		private val CALORIE_TARGET = intPreferencesKey("calorie_target")
		private val PROTEIN_TARGET = intPreferencesKey("protein_target")
		private val CARBS_TARGET = intPreferencesKey("carbs_target")
		private val FAT_TARGET = intPreferencesKey("fat_target")
		private val API_BASE_URL = stringPreferencesKey("api_base_url")
		private val DISPLAY_NAME = stringPreferencesKey("display_name")
		private val GOAL_TYPE = stringPreferencesKey("goal_type")
		private val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")

		fun normalizeBaseUrl(url: String): String {
			val trimmed = url.trim().ifBlank { ApiHost.resolve(null) }
			return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
		}
	}
}
