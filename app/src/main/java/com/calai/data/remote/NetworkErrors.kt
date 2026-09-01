package com.calai.data.remote

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUserMessage(fallback: String, apiUrl: String? = null): String {
	val hostHint = apiUrl?.let { " Tried $it." }.orEmpty()
	generateSequence(this) { it.cause }.forEach { error ->
		when (error) {
			is UnknownHostException, is ConnectException ->
				return "Can't reach Cal.ai.$hostHint On a phone use http://127.0.0.1:8000/ with USB connected, or your PC LAN IP in Settings. 10.0.2.2 only works on the emulator."
			is SocketTimeoutException ->
				return "The request timed out.$hostHint If this is a phone, 10.0.2.2 will hang — set Settings to http://127.0.0.1:8000/ (USB) or your PC IP, and keep the backend running."
			is HttpException -> {
				val detail = error.response()?.errorBody()?.string()?.take(180)
				return if (!detail.isNullOrBlank()) "$fallback $detail" else "$fallback HTTP ${error.code()}."
			}
		}
	}
	val detail = message?.takeIf { it.isNotBlank() }
	return if (detail != null) "$fallback $detail" else fallback
}
