package com.calai.domain.model

import java.util.Calendar

object MealTypes {
	const val Breakfast = "Breakfast"
	const val Lunch = "Lunch"
	const val Snack = "Snack"
	const val Dinner = "Dinner"

	val all = listOf(Breakfast, Lunch, Snack, Dinner)

	fun defaultForNow(): String {
		val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
		return when (hour) {
			in 5..10 -> Breakfast
			in 11..15 -> Lunch
			in 16..21 -> Dinner
			else -> Snack
		}
	}
}
