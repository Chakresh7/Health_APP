package com.calai.domain.model

import com.calai.data.local.MealEntity

data class Meal(
	val id: Long,
	val foodName: String,
	val calories: Int,
	val proteinG: Double,
	val carbsG: Double,
	val fatG: Double,
	val mealType: String,
	val timestamp: Long
)

fun MealEntity.toDomain() = Meal(
	id = id,
	foodName = foodName,
	calories = calories,
	proteinG = proteinG,
	carbsG = carbsG,
	fatG = fatG,
	mealType = mealType,
	timestamp = timestamp
)
