package com.calai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.calai.data.local.MealEntity

private val Ink = Color(0xFF17171B)
private val Body = Color(0xFF3D3D44)
private val Soft = Color(0xFFF3F3F6)

@Composable
fun MealSummaryCard(
	meal: MealEntity,
	modifier: Modifier = Modifier,
	footer: (@Composable () -> Unit)? = null
) {
	Card(
		modifier = modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		border = BorderStroke(1.dp, Color(0xFFE6E6EA)),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
		shape = RoundedCornerShape(22.dp)
	) {
		Column(modifier = Modifier.padding(14.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				if (meal.imageUri != null) {
					AsyncImage(
						model = meal.imageUri,
						contentDescription = meal.foodName,
						contentScale = ContentScale.Crop,
						modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp))
					)
				} else {
					Box(
						modifier = Modifier.size(64.dp).background(Soft, RoundedCornerShape(16.dp)),
						contentAlignment = Alignment.Center
					) {
						Icon(Icons.Filled.Fastfood, contentDescription = null, tint = Ink, modifier = Modifier.size(26.dp))
					}
				}
				Column(
					modifier = Modifier.padding(start = 12.dp).weight(1f),
					verticalArrangement = Arrangement.spacedBy(6.dp)
				) {
					Text(
						meal.foodName,
						fontWeight = FontWeight.Bold,
						style = MaterialTheme.typography.titleMedium,
						color = Ink,
						maxLines = 2,
						overflow = TextOverflow.Ellipsis
					)
					Surface(color = Soft, shape = CircleShape) {
						Text(
							meal.mealType,
							modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
							style = MaterialTheme.typography.labelMedium,
							fontWeight = FontWeight.SemiBold,
							color = Body
						)
					}
					Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
						MacroChip("P", meal.proteinG.toInt())
						MacroChip("C", meal.carbsG.toInt())
						MacroChip("F", meal.fatG.toInt())
					}
				}
				Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
					Text("${meal.calories}", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = Ink)
					Text("kcal", style = MaterialTheme.typography.labelMedium, color = Body)
				}
			}
			footer?.invoke()
		}
	}
}

@Composable
private fun MacroChip(label: String, grams: Int) {
	Surface(color = Soft, shape = RoundedCornerShape(8.dp)) {
		Text(
			"$label ${grams}g",
			modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
			style = MaterialTheme.typography.labelMedium,
			fontWeight = FontWeight.SemiBold,
			color = Body,
			maxLines = 1
		)
	}
}
