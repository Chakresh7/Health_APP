package com.calai.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calai.data.local.ChatSessionEntity

private val SoftWhite = Color(0xFFF8F8FA)
private val Ink = Color(0xFF17171B)
private val Muted = Color(0xFF707078)

private val Suggestions = listOf(
	"How am I doing today?",
	"What can I eat with calories left?",
	"High-protein dinner under 500 kcal"
)

@Composable
fun ChatScreen(viewModel: ChatViewModel) {
	val state by viewModel.uiState.collectAsStateWithLifecycle()
	val listState = rememberLazyListState()

	LaunchedEffect(state.messages.size, state.isSending) {
		if (state.messages.isNotEmpty()) {
			listState.animateScrollToItem(state.messages.lastIndex)
		}
	}

	Column(modifier = Modifier.fillMaxSize().background(SoftWhite)) {
		Surface(color = Color.White, shadowElevation = 1.dp) {
			Row(
				modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Box(
					modifier = Modifier.size(42.dp).background(Ink, CircleShape),
					contentAlignment = Alignment.Center
				) {
					Text("C", color = Color.White, fontWeight = FontWeight.Bold)
				}
				Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
					Text("Ask Cal.ai", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
					Text(
						if (state.showingHistory) "Your chats" else if (state.activeSessionId == null) "New conversation" else "Nutrition estimates only",
						color = Muted,
						style = MaterialTheme.typography.bodySmall
					)
				}
				IconButton(onClick = { viewModel.showHistory(!state.showingHistory) }) {
					Icon(
						if (state.showingHistory) Icons.Outlined.ChatBubbleOutline else Icons.Outlined.History,
						contentDescription = "Chat history",
						tint = Ink
					)
				}
				IconButton(onClick = viewModel::newChat) {
					Icon(Icons.Outlined.Add, contentDescription = "New chat", tint = Ink)
				}
			}
		}

		if (state.showingHistory) {
			ChatHistory(
				sessions = state.sessions,
				activeSessionId = state.activeSessionId,
				onOpen = viewModel::openSession,
				onDelete = viewModel::deleteSession,
				onNew = viewModel::newChat
			)
			return
		}

		val emptyThread = state.messages.none { it.fromUser }
		LazyColumn(
			modifier = Modifier.weight(1f).fillMaxWidth(),
			state = listState,
			contentPadding = PaddingValues(20.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			if (emptyThread) {
				item {
					Card(
						modifier = Modifier.fillMaxWidth(),
						colors = CardDefaults.cardColors(containerColor = Ink),
						shape = RoundedCornerShape(28.dp),
						elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
					) {
						Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
							Text("How can I help?", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
							Text(
								"Cal.ai uses today’s log for context. Replies are informational, not medical advice.",
								color = Color(0xFFC8C8CE),
								style = MaterialTheme.typography.bodyMedium
							)
						}
					}
				}
				items(Suggestions) { suggestion ->
					Card(
						modifier = Modifier.fillMaxWidth().clickable(enabled = !state.isSending) { viewModel.send(suggestion) },
						colors = CardDefaults.cardColors(containerColor = Color.White),
						elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
						border = BorderStroke(1.dp, Color(0xFFE8E8EC)),
						shape = RoundedCornerShape(22.dp)
					) {
						Text(
							suggestion,
							modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
							fontWeight = FontWeight.SemiBold,
							color = Ink
						)
					}
				}
			} else {
				items(state.messages.size) { index ->
					val message = state.messages[index]
					if (message == WelcomeMessage && state.messages.any { it.fromUser }) return@items
					MessageBubble(message)
				}
				if (state.isSending) {
					item { Text("Cal.ai is thinking…", color = Muted) }
				}
			}
		}

		state.error?.let {
			Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 20.dp), style = MaterialTheme.typography.bodySmall)
		}

		Surface(color = Color.White, shadowElevation = 12.dp) {
			Row(
				modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(10.dp)
			) {
				OutlinedTextField(
					modifier = Modifier.weight(1f),
					value = state.input,
					onValueChange = viewModel::onInputChange,
					placeholder = { Text("Message Cal.ai") },
					enabled = !state.isSending,
					shape = RoundedCornerShape(28.dp),
					colors = OutlinedTextFieldDefaults.colors(
						focusedBorderColor = Ink,
						unfocusedBorderColor = Color(0xFFE2E2E8),
						unfocusedContainerColor = Color(0xFFF5F5F7),
						focusedContainerColor = Color.White
					)
				)
				IconButton(
					onClick = { viewModel.send() },
					enabled = !state.isSending && state.input.isNotBlank(),
					modifier = Modifier.size(52.dp).background(
						if (!state.isSending && state.input.isNotBlank()) Ink else Color(0xFFD0D0D4),
						CircleShape
					)
				) {
					Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "Send", tint = Color.White)
				}
			}
		}
	}
}

@Composable
private fun MessageBubble(message: ChatMessage) {
	val fromUser = message.fromUser
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = if (fromUser) Alignment.End else Alignment.Start
	) {
		if (!fromUser) {
			Text("Cal.ai", color = Muted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
		}
		Surface(
			color = if (fromUser) Ink else Color.White,
			shadowElevation = if (fromUser) 0.dp else 1.dp,
			shape = RoundedCornerShape(
				topStart = 20.dp,
				topEnd = 20.dp,
				bottomStart = if (fromUser) 20.dp else 6.dp,
				bottomEnd = if (fromUser) 6.dp else 20.dp
			),
			modifier = Modifier.widthIn(max = 300.dp)
		) {
			Text(
				text = message.text,
				color = if (fromUser) Color.White else Ink,
				modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
			)
		}
	}
}

@Composable
private fun ChatHistory(
	sessions: List<ChatSessionEntity>,
	activeSessionId: Long?,
	onOpen: (Long) -> Unit,
	onDelete: (Long) -> Unit,
	onNew: () -> Unit
) {
	Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
		Button(
			onClick = onNew,
			modifier = Modifier.fillMaxWidth().height(52.dp),
			colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = Color.White),
			shape = RoundedCornerShape(28.dp)
		) {
			Icon(Icons.Outlined.Add, contentDescription = null)
			Text("  New chat")
		}
		Spacer(modifier = Modifier.height(16.dp))
		if (sessions.isEmpty()) {
			Text("No chats yet. Start one and it will show up here.", color = Muted)
		} else {
			LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
				items(sessions, key = { it.id }) { session ->
					Card(
						colors = CardDefaults.cardColors(containerColor = Color.White),
						border = BorderStroke(1.dp, if (session.id == activeSessionId) Ink else Color(0xFFE2E2E5)),
						shape = RoundedCornerShape(20.dp),
						modifier = Modifier.fillMaxWidth().clickable { onOpen(session.id) }
					) {
						Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
							Box(
								modifier = Modifier.size(40.dp).background(SoftWhite, CircleShape),
								contentAlignment = Alignment.Center
							) {
								Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = Ink)
							}
							Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
								Text(session.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
								Text(if (session.id == activeSessionId) "Open now" else "Tap to open", color = Muted, style = MaterialTheme.typography.bodySmall)
							}
							IconButton(onClick = { onDelete(session.id) }) {
								Icon(Icons.Outlined.Delete, contentDescription = "Delete chat", tint = Muted)
							}
						}
					}
				}
			}
		}
	}
}
