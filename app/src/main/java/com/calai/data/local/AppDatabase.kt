package com.calai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
	entities = [MealEntity::class, ChatSessionEntity::class, ChatMessageEntity::class],
	version = 2,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun mealDao(): MealDao
	abstract fun chatDao(): ChatDao

	companion object {
		private val MIGRATION_1_2 = object : Migration(1, 2) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL(
					"""
					CREATE TABLE IF NOT EXISTS chat_sessions (
						id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
						title TEXT NOT NULL,
						updatedAt INTEGER NOT NULL
					)
					""".trimIndent()
				)
				db.execSQL(
					"""
					CREATE TABLE IF NOT EXISTS chat_messages (
						id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
						sessionId INTEGER NOT NULL,
						text TEXT NOT NULL,
						fromUser INTEGER NOT NULL,
						createdAt INTEGER NOT NULL,
						FOREIGN KEY(sessionId) REFERENCES chat_sessions(id) ON DELETE CASCADE
					)
					""".trimIndent()
				)
				db.execSQL("CREATE INDEX IF NOT EXISTS index_chat_messages_sessionId ON chat_messages(sessionId)")
			}
		}

		fun create(context: Context): AppDatabase = Room.databaseBuilder(
			context,
			AppDatabase::class.java,
			"cal_ai.db"
		).addMigrations(MIGRATION_1_2).build()
	}
}
