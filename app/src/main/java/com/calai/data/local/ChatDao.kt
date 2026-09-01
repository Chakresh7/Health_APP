package com.calai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
	@Query("SELECT * FROM chat_sessions ORDER BY updatedAt DESC")
	fun observeSessions(): Flow<List<ChatSessionEntity>>

	@Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY createdAt ASC")
	fun observeMessages(sessionId: Long): Flow<List<ChatMessageEntity>>

	@Insert
	suspend fun insertSession(session: ChatSessionEntity): Long

	@Update
	suspend fun updateSession(session: ChatSessionEntity)

	@Insert
	suspend fun insertMessage(message: ChatMessageEntity)

	@Query("DELETE FROM chat_sessions WHERE id = :sessionId")
	suspend fun deleteSession(sessionId: Long)
}
