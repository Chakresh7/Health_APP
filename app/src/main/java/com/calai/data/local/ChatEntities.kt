package com.calai.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val title: String,
	val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
	tableName = "chat_messages",
	foreignKeys = [
		ForeignKey(
			entity = ChatSessionEntity::class,
			parentColumns = ["id"],
			childColumns = ["sessionId"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [Index("sessionId")]
)
data class ChatMessageEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val sessionId: Long,
	val text: String,
	val fromUser: Boolean,
	val createdAt: Long = System.currentTimeMillis()
)
