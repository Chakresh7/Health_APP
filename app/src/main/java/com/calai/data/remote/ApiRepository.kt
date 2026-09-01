package com.calai.data.remote

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ApiRepository(private val api: CalAiApi) {
	suspend fun analyzeFood(contentResolver: ContentResolver, imageUri: Uri): NutritionAnalysis {
		val imageBytes = contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
			?: error("Unable to read selected image")
		val contentType = contentResolver.getType(imageUri) ?: "image/jpeg"
		require(contentType in setOf("image/jpeg", "image/png", "image/webp")) {
			"Unsupported image type"
		}
		val requestBody = imageBytes.toRequestBody(contentType.toMediaType())
		val imagePart = MultipartBody.Part.createFormData("image", "meal.jpg", requestBody)
		return api.analyzeFood(imagePart)
	}

	suspend fun analyzeFoodText(description: String): NutritionAnalysis =
		api.analyzeFoodText(FoodTextRequest(description))

	suspend fun chat(request: ChatRequest): ChatResponse = api.chat(request)

	suspend fun health(): HealthResponse = api.health()
}
