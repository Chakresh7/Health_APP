package com.calai.data.remote

import com.calai.BuildConfig
import com.calai.data.local.UserPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
	@Volatile
	private var cachedUrl: String? = null

	@Volatile
	private var cachedApi: CalAiApi? = null

	fun api(baseUrl: String = BuildConfig.API_BASE_URL): CalAiApi {
		val normalized = UserPreferences.normalizeBaseUrl(baseUrl)
		cachedApi?.let { existing ->
			if (cachedUrl == normalized) return existing
		}
		synchronized(this) {
			cachedApi?.let { existing ->
				if (cachedUrl == normalized) return existing
			}
			val api = createApi(normalized)
			cachedUrl = normalized
			cachedApi = api
			return api
		}
	}

	fun repository(baseUrl: String = BuildConfig.API_BASE_URL): ApiRepository =
		ApiRepository(api(baseUrl))

	private fun createApi(baseUrl: String): CalAiApi {
		val logging = HttpLoggingInterceptor().apply {
			level = if (BuildConfig.DEBUG) {
				HttpLoggingInterceptor.Level.BASIC
			} else {
				HttpLoggingInterceptor.Level.NONE
			}
		}
		val httpClient = OkHttpClient.Builder()
			.connectTimeout(8, TimeUnit.SECONDS)
			.readTimeout(90, TimeUnit.SECONDS)
			.writeTimeout(90, TimeUnit.SECONDS)
			.retryOnConnectionFailure(true)
			.addInterceptor(logging)
			.build()
		return Retrofit.Builder()
			.baseUrl(baseUrl)
			.client(httpClient)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
			.create(CalAiApi::class.java)
	}
}
