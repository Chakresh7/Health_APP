package com.calai.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val SoftWhite = Color(0xFFF8F8FA)
private val Ink = Color(0xFF17171B)

@Composable
fun LoginScreen(
	viewModel: AuthViewModel,
	onSignedIn: () -> Unit
) {
	val state by viewModel.uiState.collectAsStateWithLifecycle()
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(SoftWhite)
			.padding(24.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text("Cal.ai", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineMedium, color = Ink)
		Text("Welcome back", color = Color(0xFF707078), modifier = Modifier.padding(bottom = 16.dp))
		GoogleSignInSection(
			state = state,
			title = "Sign in to continue",
			subtitle = "Use the same Google account you used when you set up Cal.ai. Sign-in and sign-up are the same button.",
			onSignIn = { activity -> viewModel.signInWithGoogle(activity, onSignedIn) }
		)
	}
}
