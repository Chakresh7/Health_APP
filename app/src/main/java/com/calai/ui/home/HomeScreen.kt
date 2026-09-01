package com.calai.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calai.data.local.MealRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val SoftWhite = Color(0xFFF8F8FA)
private val Ink = Color(0xFF17171B)
private val Muted = Color(0xFF707078)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onScanFood: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNotifications: () -> Unit,
    onAddManually: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val caloriesLeft = (state.calorieTarget - state.calories).coerceAtLeast(0)
    val calorieProgress = if (state.calorieTarget == 0) 0f else (state.calories / state.calorieTarget.toFloat()).coerceIn(0f, 1f)
    val dayLabel = if (state.isToday) {
        "Today"
    } else {
        SimpleDateFormat("EEE, MMM d", Locale.US).format(Date(state.selectedDayStart))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftWhite)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(state.displayName, dayLabel, state.notifications.isNotEmpty(), onOpenNotifications, onOpenSettings)
            Spacer(modifier = Modifier.height(12.dp))
            DateRail(
                selectedDayStart = state.selectedDayStart,
                onSelectDay = viewModel::selectDay
            )
        }
        item {
            CalorieHero(
                caloriesLeft = caloriesLeft,
                progress = calorieProgress,
                consumed = state.calories,
                target = state.calorieTarget,
                caloriesOverTarget = state.caloriesOverTarget
            )
        }
        item { InsightCard(state.insight) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MacroCard("Protein", state.proteinG.toInt(), state.proteinTarget, Icons.Filled.Favorite, Modifier.weight(1f))
                MacroCard("Carbs", state.carbsG.toInt(), state.carbsTarget, Icons.Filled.Grain, Modifier.weight(1f))
                MacroCard("Fats", state.fatG.toInt(), state.fatTarget, Icons.Filled.WaterDrop, Modifier.weight(1f))
            }
        }
        item {
            Text(
                if (state.isToday) "Today's meals" else "Meals · $dayLabel",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        if (state.meals.isEmpty()) {
            item { EmptyMeals(state.isToday, onScanFood, onAddManually) }
        } else {
            items(state.meals, key = { it.id }) { meal ->
                com.calai.ui.components.MealSummaryCard(meal)
            }
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun Header(
    displayName: String,
    dayLabel: String,
    hasNotifications: Boolean,
    onOpenNotifications: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                if (displayName.isBlank() || displayName == "there") "Cal AI" else "Hi, $displayName",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(dayLabel, color = Muted, style = MaterialTheme.typography.bodyMedium)
        }
        Box {
            IconButton(onClick = onOpenNotifications) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Ink)
            }
            if (hasNotifications) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                        .size(8.dp)
                        .background(Ink, CircleShape)
                )
            }
        }
        IconButton(onClick = onOpenSettings) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Ink)
        }
    }
}

@Composable
private fun DateRail(selectedDayStart: Long, onSelectDay: (Long) -> Unit) {
    val today = Calendar.getInstance()
    val dates = (-5..1).map { offset ->
        Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            add(Calendar.DAY_OF_YEAR, offset)
        }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        dates.forEach { date ->
            val dayStart = MealRepository.startOfDayMillis(date.timeInMillis)
            val selected = dayStart == selectedDayStart
            Column(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSelectDay(dayStart) }
                    .padding(horizontal = 2.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    SimpleDateFormat("EEE", Locale.US).format(date.time),
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) Ink else Muted,
                    style = MaterialTheme.typography.labelMedium
                )
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(if (selected) Ink else Color(0xFFE8E8EC), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        date.get(Calendar.DAY_OF_MONTH).toString(),
                        color = if (selected) Color.White else Ink,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CalorieHero(
    caloriesLeft: Int,
    progress: Float,
    consumed: Int,
    target: Int,
    caloriesOverTarget: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E2E5)),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (caloriesOverTarget > 0) {
                    Text(caloriesOverTarget.toString(), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold)
                    Text("Calories over target", style = MaterialTheme.typography.titleMedium)
                } else {
                    Text(caloriesLeft.toString(), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold)
                    Text("Calories left", style = MaterialTheme.typography.titleMedium)
                }
                Text("$consumed / ${"%,d".format(target)} kcal consumed", color = Muted, style = MaterialTheme.typography.bodySmall)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(8.dp).clip(CircleShape),
                    color = Ink,
                    trackColor = Color(0xFFE7E7EA),
                    strokeCap = StrokeCap.Round
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(92.dp)) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(92.dp),
                    color = Color(0xFFE7E7EA),
                    strokeWidth = 8.dp,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(92.dp),
                    color = Ink,
                    strokeWidth = 8.dp,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(Ink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.LocalFireDepartment,
                            contentDescription = "Calories",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                        Text("kcal", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightCard(insight: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E2E5)),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Cal.ai insight", fontWeight = FontWeight.Bold)
            Text(insight, color = Muted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun MacroCard(
    label: String,
    consumed: Int,
    target: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier
) {
    val remaining = (target - consumed).coerceAtLeast(0)
    val progress = if (target == 0) 0f else (consumed / target.toFloat()).coerceIn(0f, 1f)
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E2E5)),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 14.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(58.dp)) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(58.dp),
                    color = Color(0xFFEEEEF1),
                    strokeWidth = 6.dp,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(58.dp),
                    color = Ink,
                    strokeWidth = 6.dp,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )
                Icon(icon, contentDescription = label, tint = Ink, modifier = Modifier.size(18.dp))
            }
            Text("${remaining}g", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Text(label, color = Muted, style = MaterialTheme.typography.labelMedium)
            Text("$consumed / ${target}g", color = Muted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun EmptyMeals(isToday: Boolean, onScanFood: () -> Unit, onAddManually: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E2E5)),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(if (isToday) "No meals logged yet" else "Nothing logged this day", fontWeight = FontWeight.Bold)
            Text(
                if (isToday) "Use + to scan a meal, or add one manually." else "Pick another day, or add a meal if this date is missing.",
                color = Muted
            )
            if (isToday) {
                TextButton(onClick = onScanFood) { Text("Scan a meal") }
                TextButton(onClick = onAddManually) { Text("Add meal manually") }
            }
        }
    }
}
