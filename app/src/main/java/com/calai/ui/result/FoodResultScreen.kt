package com.calai.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calai.data.remote.NutritionAnalysis
import com.calai.domain.model.MealTypes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FoodResultScreen(
	analysis: NutritionAnalysis,
	initialMealType: String = MealTypes.defaultForNow(),
	title: String = "Review estimate",
	primaryActionLabel: String = "Add to diary",
	showRetake: Boolean = true,
	onSave: (NutritionAnalysis, String) -> Unit,
	onRetake: () -> Unit = {},
	onCancel: (() -> Unit)? = null
) {
	var foodName by remember(analysis) { mutableStateOf(analysis.food_name) }
	var calories by remember(analysis) { mutableStateOf(if (analysis.estimated_calories == 0) "" else analysis.estimated_calories.toString()) }
	var protein by remember(analysis) { mutableStateOf(if (analysis.protein_g == 0.0) "" else analysis.protein_g.toString()) }
	var carbs by remember(analysis) { mutableStateOf(if (analysis.carbs_g == 0.0) "" else analysis.carbs_g.toString()) }
	var fat by remember(analysis) { mutableStateOf(if (analysis.fat_g == 0.0) "" else analysis.fat_g.toString()) }
	var mealType by remember(analysis, initialMealType) { mutableStateOf(initialMealType) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(24.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
		if (analysis.portion_estimate.isNotBlank()) {
			Text("Portion: ${analysis.portion_estimate}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
		}
		if (analysis.components.isNotEmpty()) {
			Text("Detected: ${analysis.components.joinToString(", ")}")
		}
		NutritionField("Food name", foodName) { foodName = it }
		NutritionField("Calories", calories) { calories = it }
		Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
			NutritionField("Protein (g)", protein, Modifier.weight(1f)) { protein = it }
			NutritionField("Carbs (g)", carbs, Modifier.weight(1f)) { carbs = it }
			NutritionField("Fat (g)", fat, Modifier.weight(1f)) { fat = it }
		}
		Text("Meal type", fontWeight = FontWeight.SemiBold)
		FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
			MealTypes.all.forEach { type ->
				FilterChip(
					selected = mealType == type,
					onClick = { mealType = type },
					label = { Text(type) }
				)
			}
		}
		if (analysis.confidence > 0) {
			Text("AI confidence: ${(analysis.confidence * 100).toInt()}%")
		}
		Text("These values are estimates and may vary with portion size. Not medical advice.")
		Button(
			modifier = Modifier.fillMaxWidth(),
			onClick = {
				val name = foodName.trim().ifBlank { "Meal" }
				onSave(
					analysis.copy(
						food_name = name,
						estimated_calories = calories.toIntOrNull() ?: analysis.estimated_calories,
						protein_g = protein.toDoubleOrNull() ?: analysis.protein_g,
						carbs_g = carbs.toDoubleOrNull() ?: analysis.carbs_g,
						fat_g = fat.toDoubleOrNull() ?: analysis.fat_g
					),
					mealType
				)
			}
		) { Text(primaryActionLabel) }
		if (showRetake) {
			OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onRetake) { Text("Retake") }
		}
		if (onCancel != null) {
			OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onCancel) { Text("Cancel") }
		}
	}
}

@Composable
private fun NutritionField(
	label: String,
	value: String,
	modifier: Modifier = Modifier,
	onValueChange: (String) -> Unit
) {
	OutlinedTextField(
		modifier = modifier,
		value = value,
		onValueChange = onValueChange,
		label = { Text(label) },
		singleLine = true
	)
}
