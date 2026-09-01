package com.calai.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calai.ui.home.AppNotification

private val SoftWhite = Color(0xFFF8F8FA)
private val Ink = Color(0xFF17171B)
private val Muted = Color(0xFF5C5C64)

@Composable
fun NotificationsScreen(
	notifications: List<AppNotification>,
	onBack: () -> Unit
) {
	Column(modifier = Modifier.fillMaxSize().background(SoftWhite).padding(20.dp)) {
		Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.weight(1f)) {
				Text("Notifications", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
				Text("Reminders from today’s log", color = Muted)
			}
			TextButton(onClick = onBack) { Text("Back") }
		}
		if (notifications.isEmpty()) {
			Text("You’re all caught up.", color = Muted, modifier = Modifier.padding(top = 24.dp))
		} else {
			LazyColumn(
				modifier = Modifier.padding(top = 16.dp),
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				items(notifications, key = { it.id }) { item ->
					Card(
						colors = CardDefaults.cardColors(containerColor = Color.White),
						shape = RoundedCornerShape(20.dp),
						modifier = Modifier.fillMaxWidth()
					) {
						Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
							Box(
								modifier = Modifier.size(42.dp).background(Color(0xFFF0F0F3), CircleShape),
								contentAlignment = Alignment.Center
							) {
								Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Ink)
							}
							Column(modifier = Modifier.padding(start = 12.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
								Text(item.title, fontWeight = FontWeight.Bold, color = Ink)
								Text(item.body, color = Muted, style = MaterialTheme.typography.bodyMedium)
								Text(item.timeLabel, color = Muted, style = MaterialTheme.typography.labelSmall)
							}
						}
					}
				}
			}
		}
	}
}
