package com.calai.ui.diary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calai.data.local.MealEntity
import com.calai.ui.components.MealSummaryCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val SoftWhite = Color(0xFFF8F8FA)
private val Ink = Color(0xFF17171B)
private val Muted = Color(0xFF707078)

@Composable
fun DiaryScreen(
	viewModel: DiaryViewModel,
	onEditMeal: (MealEntity) -> Unit,
	onAddManually: () -> Unit
) {
	val meals by viewModel.meals.collectAsStateWithLifecycle()
	var selectedTab by remember { mutableIntStateOf(0) }
	val visibleMeals = if (selectedTab == 0) meals.filter(::isToday) else meals.filterNot(::isToday)
	val totalCalories = visibleMeals.sumOf { it.calories }
	val protein = visibleMeals.sumOf { it.proteinG }.toInt()
	val carbs = visibleMeals.sumOf { it.carbsG }.toInt()
	val fat = visibleMeals.sumOf { it.fatG }.toInt()
	val grouped = visibleMeals.groupBy { dayKey(it.timestamp) }.toList()

	LazyColumn(
		modifier = Modifier.fillMaxSize().background(SoftWhite),
		contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
				Column(modifier = Modifier.weight(1f)) {
					Text("Diary", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
					Text("Your meals, by day", color = Muted)
				}
				TextButton(onClick = onAddManually) { Text("Add") }
			}
		}
		item {
			Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				FilterChip(
					selected = selectedTab == 0,
					onClick = { selectedTab = 0 },
					label = { Text("Today") },
					colors = FilterChipDefaults.filterChipColors(
						selectedContainerColor = Ink,
						selectedLabelColor = Color.White
					)
				)
				FilterChip(
					selected = selectedTab == 1,
					onClick = { selectedTab = 1 },
					label = { Text("History") },
					colors = FilterChipDefaults.filterChipColors(
						selectedContainerColor = Ink,
						selectedLabelColor = Color.White
					)
				)
			}
		}
		item {
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(containerColor = Ink),
				shape = RoundedCornerShape(28.dp)
			) {
				Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
					Text(if (selectedTab == 0) "Today" else "Earlier days", color = Color(0xFFB8B8BE), style = MaterialTheme.typography.labelLarge)
					Text("$totalCalories kcal", color = Color.White, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold)
					Text(
						"${visibleMeals.size} meals  ·  P ${protein}g  ·  C ${carbs}g  ·  F ${fat}g",
						color = Color(0xFFD0D0D6),
						style = MaterialTheme.typography.bodyMedium
					)
				}
			}
		}
		if (visibleMeals.isEmpty()) {
			item {
				Card(
					colors = CardDefaults.cardColors(containerColor = Color.White),
					border = BorderStroke(1.dp, Color(0xFFE2E2E5)),
					shape = RoundedCornerShape(24.dp),
					modifier = Modifier.fillMaxWidth()
				) {
					Text(
						if (selectedTab == 0) "Nothing logged today yet." else "Past meals will appear here.",
						modifier = Modifier.padding(20.dp),
						color = Muted
					)
				}
			}
		} else {
			grouped.forEach { (day, dayMeals) ->
				if (selectedTab == 1) {
					item {
						Text(formatDay(dayMeals.first().timestamp), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
					}
				}
				items(dayMeals, key = { it.id }) { meal ->
					MealSummaryCard(
						meal = meal,
						footer = {
							Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
								Text(formatClock(meal.timestamp), color = Muted, style = MaterialTheme.typography.labelMedium)
								Spacer(modifier = Modifier.weight(1f))
								TextButton(onClick = { onEditMeal(meal) }) { Text("Edit") }
								TextButton(onClick = { viewModel.deleteMeal(meal) }) { Text("Delete") }
							}
						}
					)
				}
			}
		}
		item { Spacer(modifier = Modifier.height(12.dp)) }
	}
}

private fun isToday(meal: MealEntity): Boolean =
	dayKey(meal.timestamp) == dayKey(System.currentTimeMillis())

private fun dayKey(timestamp: Long): String =
	SimpleDateFormat("yyyyMMdd", Locale.US).format(Date(timestamp))

private fun formatDay(timestamp: Long): String =
	SimpleDateFormat("EEEE, MMM d", Locale.US).format(Date(timestamp))

private fun formatClock(timestamp: Long): String =
	SimpleDateFormat("h:mm a", Locale.US).format(Date(timestamp))
