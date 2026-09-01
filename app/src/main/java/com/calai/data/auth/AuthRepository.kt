package com.calai.data.auth

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.calai.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.createSupabaseClient
import java.security.MessageDigest
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

data class AuthUiState(
	val isConfigured: Boolean = false,
	val isInitializing: Boolean = true,
	val isSignedIn: Boolean = false,
	val isBusy: Boolean = false,
	val email: String? = null,
	val displayName: String? = null,
	val error: String? = null
)

class AuthRepository(context: Context) {
	private val appContext = context.applicationContext
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
	private val _uiState = MutableStateFlow(
		AuthUiState(
			isConfigured = isConfigured(),
			isInitializing = isConfigured()
		)
	)
	val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

	private val client: SupabaseClient? = if (isConfigured()) {
		createSupabaseClient(
			supabaseUrl = BuildConfig.SUPABASE_URL.trim().trimEnd('/'),
			supabaseKey = BuildConfig.SUPABASE_ANON_KEY.trim()
		) {
			install(Auth)
		}
	} else {
		null
	}

	init {
		val supabase = client
		if (supabase == null) {
			_uiState.update { it.copy(isInitializing = false, isConfigured = false) }
		} else {
			scope.launch {
				supabase.auth.sessionStatus.collect { status ->
					when (status) {
						is SessionStatus.Authenticated -> {
							val user = status.session.user
							_uiState.update {
								it.copy(
									isInitializing = false,
									isSignedIn = true,
									email = user?.email,
									displayName = user?.email,
									error = null
								)
							}
						}
						is SessionStatus.NotAuthenticated -> {
							_uiState.update {
								it.copy(
									isInitializing = false,
									isSignedIn = false,
									email = null,
									displayName = null
								)
							}
						}
						SessionStatus.Initializing -> {
							_uiState.update { it.copy(isInitializing = true) }
						}
						is SessionStatus.RefreshFailure -> {
							_uiState.update {
								it.copy(
									isInitializing = false,
									error = "Session expired. Sign in with Google again."
								)
							}
						}
						else -> Unit
					}
				}
			}
		}
	}

	fun clearError() {
		_uiState.update { it.copy(error = null) }
	}

	suspend fun signInWithGoogle(activity: Activity): Boolean {
		if (!isConfigured()) {
			_uiState.update {
				it.copy(error = "Add SUPABASE_URL, SUPABASE_ANON_KEY, and GOOGLE_WEB_CLIENT_ID to local.properties.")
			}
			return false
		}
		val supabase = client ?: return false
		_uiState.update { it.copy(isBusy = true, error = null) }
		val rawNonce = UUID.randomUUID().toString()
		val hashedNonce = sha256(rawNonce)
		val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID.trim()
		return try {
			val idToken = requestGoogleIdToken(activity, webClientId, hashedNonce)
			supabase.auth.signInWith(IDToken) {
				this.idToken = idToken
				provider = Google
				nonce = rawNonce
			}
			_uiState.update { it.copy(isBusy = false, error = null) }
			true
		} catch (e: GetCredentialCancellationException) {
			_uiState.update { it.copy(isBusy = false, error = null) }
			false
		} catch (e: GetCredentialException) {
			_uiState.update { it.copy(isBusy = false, error = friendlyCredentialError(e)) }
			false
		} catch (e: Exception) {
			_uiState.update {
				it.copy(isBusy = false, error = e.message?.takeIf { msg -> msg.isNotBlank() } ?: "Google sign-in failed.")
			}
			false
		}
	}

	suspend fun signOut() {
		_uiState.update { it.copy(isBusy = true, error = null) }
		runCatching { client?.auth?.signOut() }
		_uiState.update {
			it.copy(
				isBusy = false,
				isSignedIn = false,
				email = null,
				displayName = null
			)
		}
	}

	private suspend fun requestGoogleIdToken(
		activity: Activity,
		webClientId: String,
		hashedNonce: String
	): String {
		val manager = CredentialManager.create(activity)
		val signInOption = GetSignInWithGoogleOption.Builder(webClientId)
			.setNonce(hashedNonce)
			.build()
		return try {
			fetchIdToken(manager, activity, GetCredentialRequest.Builder().addCredentialOption(signInOption).build())
		} catch (e: NoCredentialException) {
			val googleIdOption = GetGoogleIdOption.Builder()
				.setFilterByAuthorizedAccounts(false)
				.setServerClientId(webClientId)
				.setNonce(hashedNonce)
				.setAutoSelectEnabled(false)
				.build()
			fetchIdToken(manager, activity, GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build())
		}
	}

	private suspend fun fetchIdToken(
		manager: CredentialManager,
		activity: Activity,
		request: GetCredentialRequest
	): String {
		val result = manager.getCredential(context = activity, request = request)
		val credential = result.credential
		if (credential is CustomCredential &&
			credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
		) {
			return GoogleIdTokenCredential.createFrom(credential.data).idToken
		}
		error("Google did not return an ID token.")
	}

	private fun friendlyCredentialError(error: GetCredentialException): String {
		val message = (error.message.orEmpty() + " " + (error.cause?.message.orEmpty())).trim()
		return when {
			message.contains("DEVELOPER_ERROR", ignoreCase = true) ||
				message.contains("not registered", ignoreCase = true) ||
				message.contains("reauth failed", ignoreCase = true) ||
				message.contains("[16]", ignoreCase = true) ||
				message.contains("10:", ignoreCase = true) ->
				"Google needs an Android OAuth client for this phone. In Google Cloud → Credentials, create OAuth client type Android, package com.calai, SHA-1 5D:95:8D:5E:B5:AF:28:44:FE:42:2D:9B:D8:24:FE:BE:8C:9D:2E:B9. Wait a minute, then try again."
			message.contains("canceled", ignoreCase = true) ||
				message.contains("cancelled", ignoreCase = true) ->
				"Sign-in was cancelled."
			else -> message.ifBlank { "Google sign-in failed." }
		}
	}

	companion object {
		fun isConfigured(): Boolean =
			BuildConfig.SUPABASE_URL.isNotBlank() &&
				BuildConfig.SUPABASE_ANON_KEY.isNotBlank() &&
				BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank()

		private fun sha256(value: String): String {
			val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
			return digest.joinToString("") { "%02x".format(it) }
		}
	}
}
