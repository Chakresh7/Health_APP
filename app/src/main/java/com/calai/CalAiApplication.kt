package com.calai

import android.app.Application
import com.calai.data.auth.AuthRepository

class CalAiApplication : Application() {
	lateinit var authRepository: AuthRepository
		private set

	override fun onCreate() {
		super.onCreate()
		authRepository = AuthRepository(this)
	}
}
