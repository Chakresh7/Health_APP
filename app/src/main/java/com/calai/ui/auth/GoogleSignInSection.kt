package com.calai.ui.auth

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calai.data.auth.AuthUiState

private val Ink = Color(0xFF17171B)
private val Muted = Color(0xFF707078)

@Composable
fun GoogleSignInSection(
	state: AuthUiState,
	title: String,
	subtitle: String,
	onSignIn: (Activity) -> Unit
) {
	val context = LocalContext.current
	val activity = context as? Activity
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
		Text(subtitle, color = Muted)
		OutlinedButton(
			modifier = Modifier.fillMaxWidth().height(54.dp),
			shape = RoundedCornerShape(28.dp),
			border = BorderStroke(1.dp, Color(0xFFE2E2E8)),
			colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Ink),
			enabled = !state.isBusy && activity != null,
			onClick = { activity?.let(onSignIn) }
		) {
			if (state.isBusy) {
				CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Ink, strokeWidth = 2.dp)
			} else {
				Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
					Box(
						modifier = Modifier.size(22.dp).background(Color(0xFFF1F1F4), CircleShape),
						contentAlignment = Alignment.Center
					) {
						Text("G", fontWeight = FontWeight.Bold, color = Ink)
					}
					Text("Continue with Google", fontWeight = FontWeight.SemiBold)
				}
			}
		}
		if (!state.isConfigured) {
			Text(
				"Add your Supabase URL, anon key, and Google Web client ID to local.properties, then rebuild.",
				color = Muted,
				style = MaterialTheme.typography.bodySmall
			)
		}
		state.error?.let {
			Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
		}
		Spacer(Modifier.height(4.dp))
	}
}
