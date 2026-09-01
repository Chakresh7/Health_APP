package com.calai.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(
	viewModel: SettingsViewModel,
	authViewModel: com.calai.ui.auth.AuthViewModel,
	onBack: () -> Unit,
	onLoggedOut: () -> Unit
) {
	val state by viewModel.uiState.collectAsStateWithLifecycle()
	val authState by authViewModel.uiState.collectAsStateWithLifecycle()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(24.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
		Text("Account", fontWeight = FontWeight.SemiBold)
		Text(authState.email ?: "Signed in with Google")
		OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { authViewModel.signOut(onLoggedOut) }) {
			Text("Log out")
		}
		Text("Daily targets")
		OutlinedTextField(state.calorieTarget, viewModel::onCalorieChange, label = { Text("Calories (kcal)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
		OutlinedTextField(state.proteinTarget, viewModel::onProteinChange, label = { Text("Protein (g)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
		OutlinedTextField(state.carbsTarget, viewModel::onCarbsChange, label = { Text("Carbs (g)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
		OutlinedTextField(state.fatTarget, viewModel::onFatChange, label = { Text("Fat (g)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
		Text("Backend", fontWeight = FontWeight.SemiBold)
		Text("On this phone the app talks to http://127.0.0.1:8000/ through USB. Keep the cable connected and the backend running. Do not use 10.0.2.2 on a real device — that address only works in the emulator and will time out.")
		OutlinedTextField(state.apiBaseUrl, viewModel::onApiUrlChange, label = { Text("API base URL") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
		Button(modifier = Modifier.fillMaxWidth(), onClick = viewModel::save) { Text("Save settings") }
		OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = viewModel::checkConnection) { Text("Check connection") }
		state.statusMessage?.let { Text(it) }
		Text("About Cal.ai", fontWeight = FontWeight.SemiBold)
		Text("Cal.ai estimates calories and macros from photos or descriptions. Values are approximate and not medical advice.")
		OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onBack) { Text("Back") }
	}
}
