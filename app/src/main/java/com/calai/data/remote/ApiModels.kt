package com.calai.data.remote

data class NutritionAnalysis(
	val food_name: String,
	val portion_estimate: String,
	val estimated_calories: Int,
	val protein_g: Double,
	val carbs_g: Double,
	val fat_g: Double,
	val confidence: Double,
	val components: List<String> = emptyList()
)

data class FoodTextRequest(
	val text: String
)

data class ChatRequest(
	val message: String,
	val calories_consumed: Int,
	val calorie_target: Int,
	val protein_g: Double,
	val carbs_g: Double,
	val fat_g: Double,
	val protein_target: Double,
	val carbs_target: Double,
	val fat_target: Double
)

data class ChatResponse(
	val reply: String
)

data class HealthResponse(
	val status: String
)
