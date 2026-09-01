package com.calai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
	@Insert
	suspend fun insertMeal(meal: MealEntity)

	@Update
	suspend fun updateMeal(meal: MealEntity)

	@Query("SELECT * FROM meals WHERE timestamp >= :startOfDay AND timestamp < :endOfDay ORDER BY timestamp DESC")
	fun getMealsForDay(startOfDay: Long, endOfDay: Long): Flow<List<MealEntity>>

	@Query("SELECT * FROM meals ORDER BY timestamp DESC")
	fun getAllMeals(): Flow<List<MealEntity>>

	@Delete
	suspend fun deleteMeal(meal: MealEntity)
}
