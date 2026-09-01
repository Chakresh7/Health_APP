package com.calai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val foodName: String,
	val calories: Int,
	val proteinG: Double,
	val carbsG: Double,
	val fatG: Double,
	val mealType: String = "Meal",
	val timestamp: Long = System.currentTimeMillis(),
	val imageUri: String? = null
)
