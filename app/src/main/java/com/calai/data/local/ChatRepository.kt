package com.calai.data.local

import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {
	fun observeSessions(): Flow<List<ChatSessionEntity>> = chatDao.observeSessions()

	fun observeMessages(sessionId: Long): Flow<List<ChatMessageEntity>> =
		chatDao.observeMessages(sessionId)

	suspend fun createSession(title: String): Long =
		chatDao.insertSession(ChatSessionEntity(title = title))

	suspend fun renameSession(session: ChatSessionEntity, title: String) {
		chatDao.updateSession(session.copy(title = title, updatedAt = System.currentTimeMillis()))
	}

	suspend fun touchSession(session: ChatSessionEntity) {
		chatDao.updateSession(session.copy(updatedAt = System.currentTimeMillis()))
	}

	suspend fun addMessage(sessionId: Long, text: String, fromUser: Boolean) {
		chatDao.insertMessage(
			ChatMessageEntity(sessionId = sessionId, text = text, fromUser = fromUser)
		)
	}

	suspend fun deleteSession(sessionId: Long) = chatDao.deleteSession(sessionId)
}
