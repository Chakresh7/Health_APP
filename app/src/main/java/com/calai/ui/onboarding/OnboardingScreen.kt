package com.calai.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calai.ui.auth.AuthViewModel
import com.calai.ui.auth.GoogleSignInSection

private val SoftWhite = Color(0xFFF8F8FA)
private val Ink = Color(0xFF17171B)
private val Muted = Color(0xFF707078)

data class OnboardingAnswers(
	val name: String,
	val goal: String,
	val activity: String,
	val calorieTarget: Int,
	val proteinTarget: Int,
	val carbsTarget: Int,
	val fatTarget: Int
)

@Composable
fun OnboardingScreen(
	authViewModel: AuthViewModel,
	onFinished: (OnboardingAnswers) -> Unit
) {
	var step by remember { mutableIntStateOf(0) }
	var name by remember { mutableStateOf("") }
	var goal by remember { mutableStateOf("maintain") }
	var activity by remember { mutableStateOf("medium") }
	var finishing by remember { mutableStateOf(false) }
	val plan = remember(goal, activity) { targetsFor(goal, activity) }
	val authState by authViewModel.uiState.collectAsStateWithLifecycle()
	val lastStep = 3

	fun answers() = OnboardingAnswers(
		name = name.trim().ifBlank { "there" },
		goal = goal,
		activity = activity,
		calorieTarget = plan.calories,
		proteinTarget = plan.protein,
		carbsTarget = plan.carbs,
		fatTarget = plan.fat
	)

	fun finish() {
		if (finishing) return
		finishing = true
		onFinished(answers())
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(SoftWhite)
			.imePadding()
			.verticalScroll(rememberScrollState())
			.padding(24.dp)
	) {
		Text("Cal.ai", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineMedium)
		Text("Let’s set up your plan", color = Muted, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
		LinearProgressIndicator(
			progress = { (step + 1) / 4f },
			modifier = Modifier.fillMaxWidth().height(6.dp),
			color = Ink,
			trackColor = Color(0xFFE7E7EA)
		)
		Spacer(Modifier.height(28.dp))

		when (step) {
			0 -> {
				Text("What should we call you?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
				Text("This stays on your phone.", color = Muted, modifier = Modifier.padding(top = 8.dp, bottom = 20.dp))
				OutlinedTextField(
					value = name,
					onValueChange = { name = it },
					modifier = Modifier.fillMaxWidth(),
					singleLine = true,
					label = { Text("First name") },
					shape = RoundedCornerShape(16.dp)
				)
			}
			1 -> {
				Text("What’s your goal?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
				Text("We’ll set calorie and macro targets from this.", color = Muted, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
				ChoiceCard("Lose weight", "A slight calorie deficit, more protein.", goal == "lose") { goal = "lose" }
				ChoiceCard("Maintain", "Stay around your current intake.", goal == "maintain") { goal = "maintain" }
				ChoiceCard("Gain weight", "A surplus to support muscle or mass.", goal == "gain") { goal = "gain" }
			}
			2 -> {
				Text("How active are you?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
				Text("This adjusts your daily calorie target.", color = Muted, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
				ChoiceCard("Mostly sitting", "Desk work, light walking.", activity == "low") { activity = "low" }
				ChoiceCard("On my feet some days", "Walking or 2–3 workouts a week.", activity == "medium") { activity = "medium" }
				ChoiceCard("Very active", "Training most days.", activity == "high") { activity = "high" }
			}
			else -> {
				val label = when (goal) {
					"lose" -> "Lose weight"
					"gain" -> "Gain weight"
					else -> "Maintain"
				}
				Text("Your plan is ready", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
				Text(
					if (name.isBlank()) "Sign in with Google to save this plan. You can change targets later in Settings."
					else "Nice to meet you, ${name.trim()}. Sign in with Google to save this plan.",
					color = Muted,
					modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
				)
				Card(
					colors = CardDefaults.cardColors(containerColor = Color.White),
					border = BorderStroke(1.dp, Color(0xFFE2E2E5)),
					shape = RoundedCornerShape(24.dp),
					modifier = Modifier.fillMaxWidth()
				) {
					Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
						Text(label, fontWeight = FontWeight.Bold)
						Text("${plan.calories} kcal / day", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
						Text("Protein ${plan.protein}g  ·  Carbs ${plan.carbs}g  ·  Fat ${plan.fat}g", color = Muted)
					}
				}
				Spacer(Modifier.height(20.dp))
				if (authState.isSignedIn) {
					Button(
						modifier = Modifier.fillMaxWidth().height(54.dp),
						colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = Color.White),
						shape = RoundedCornerShape(28.dp),
						enabled = !finishing,
						onClick = { finish() }
					) {
						Text("Go to Home")
					}
				} else {
					GoogleSignInSection(
						state = authState,
						title = "Create your account",
						subtitle = "New and returning Google accounts both work here.",
						onSignIn = { activityContext ->
							authViewModel.signInWithGoogle(activityContext, onSuccess = { finish() })
						}
					)
				}
			}
		}

		Spacer(Modifier.height(28.dp))
		if (step < lastStep) {
			Button(
				modifier = Modifier.fillMaxWidth().height(54.dp),
				colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = Color.White),
				shape = RoundedCornerShape(28.dp),
				enabled = step > 0 || name.isNotBlank(),
				onClick = { step += 1 }
			) {
				Text("Continue")
			}
		}
		if (step > 0) {
			TextButton(
				modifier = Modifier.fillMaxWidth(),
				enabled = !finishing && !authState.isBusy,
				onClick = {
					authViewModel.clearError()
					step -= 1
				}
			) { Text("Back") }
		}
	}
}

@Composable
private fun ChoiceCard(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
	Card(
		modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable(onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = if (selected) Ink else Color.White),
		border = BorderStroke(1.dp, if (selected) Ink else Color(0xFFE2E2E5)),
		shape = RoundedCornerShape(20.dp)
	) {
		Column(Modifier.padding(16.dp)) {
			Text(title, fontWeight = FontWeight.Bold, color = if (selected) Color.White else Ink)
			Text(subtitle, color = if (selected) Color(0xFFD8D8DC) else Muted, style = MaterialTheme.typography.bodySmall)
		}
	}
}

private data class MacroPlan(val calories: Int, val protein: Int, val carbs: Int, val fat: Int)

private fun targetsFor(goal: String, activity: String): MacroPlan {
	val activityDelta = when (activity) {
		"low" -> -150
		"high" -> 250
		else -> 0
	}
	return when (goal) {
		"lose" -> MacroPlan(1_700 + activityDelta, 120, 150, 50)
		"gain" -> MacroPlan(2_400 + activityDelta, 130, 250, 75)
		else -> MacroPlan(2_000 + activityDelta, 100, 200, 65)
	}
}
