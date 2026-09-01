package com.calai.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CalAiApi {
	@Multipart
	@POST("api/v1/food/analyze")
	suspend fun analyzeFood(@Part image: MultipartBody.Part): NutritionAnalysis

	@POST("api/v1/food/text")
	suspend fun analyzeFoodText(@Body request: FoodTextRequest): NutritionAnalysis

	@POST("api/v1/chat")
	suspend fun chat(@Body request: ChatRequest): ChatResponse

	@GET("health")
	suspend fun health(): HealthResponse
}
