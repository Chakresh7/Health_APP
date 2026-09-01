package com.calai.data.remote

import android.os.Build
import com.calai.BuildConfig
import com.calai.data.local.UserPreferences

object ApiHost {
	const val EMULATOR_URL = "http://10.0.2.2:8000/"
	const val USB_DEVICE_URL = "http://127.0.0.1:8000/"

	fun isEmulator(): Boolean {
		val fingerprint = Build.FINGERPRINT.lowercase()
		val model = Build.MODEL.lowercase()
		val product = Build.PRODUCT.lowercase()
		val hardware = Build.HARDWARE.lowercase()
		return fingerprint.startsWith("generic") ||
			fingerprint.contains("emulator") ||
			model.contains("emulator") ||
			model.contains("android sdk") ||
			hardware.contains("goldfish") ||
			hardware.contains("ranchu") ||
			product.contains("sdk") ||
			product.contains("emulator")
	}

	fun resolve(storedUrl: String?): String {
		val fallback = if (isEmulator()) EMULATOR_URL else USB_DEVICE_URL
		val normalized = UserPreferences.normalizeBaseUrl(storedUrl?.ifBlank { null } ?: fallback)
		if (!isEmulator() && normalized.contains("10.0.2.2")) {
			return USB_DEVICE_URL
		}
		if (isEmulator() && (normalized.contains("127.0.0.1") || normalized.contains("localhost"))) {
			return EMULATOR_URL
		}
		return normalized
	}
}
