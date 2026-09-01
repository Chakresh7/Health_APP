package com.calai.data.local

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class MealRepository(private val mealDao: MealDao) {
    suspend fun insertMeal(meal: MealEntity) = mealDao.insertMeal(meal)

    suspend fun updateMeal(meal: MealEntity) = mealDao.updateMeal(meal)

    fun observeMealsForDay(startOfDay: Long, endOfDay: Long): Flow<List<MealEntity>> =
        mealDao.getMealsForDay(startOfDay, endOfDay)

    fun observeTodayMeals(): Flow<List<MealEntity>> {
        val start = startOfDayMillis(System.currentTimeMillis())
        return observeMealsForDay(start, start + DAY_MS)
    }

    fun observeAllMeals(): Flow<List<MealEntity>> = mealDao.getAllMeals()

    suspend fun deleteMeal(meal: MealEntity) = mealDao.deleteMeal(meal)

    companion object {
        const val DAY_MS = 24 * 60 * 60 * 1000L

        fun startOfDayMillis(timeMillis: Long): Long =
            Calendar.getInstance().apply {
                timeInMillis = timeMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
    }
}
