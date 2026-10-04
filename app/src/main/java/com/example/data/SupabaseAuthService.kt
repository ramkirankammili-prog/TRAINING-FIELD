package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AuthResult(
    val isSuccess: Boolean,
    val userEmail: String? = null,
    val accessToken: String? = null,
    val errorMessage: String? = null
)

class SupabaseAuthService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // Base URL without /rest/v1/
    private val authBaseUrl: String
        get() {
            var url = SupabaseConfig.SUPABASE_URL.trimEnd('/')
            if (url.endsWith("/rest/v1")) {
                url = url.removeSuffix("/rest/v1")
            }
            return "$url/auth/v1"
        }

    suspend fun signUp(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val url = "$authBaseUrl/signup"
            val bodyJson = JSONObject().apply {
                put("email", email)
                put("password", password)
            }.toString()

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", SupabaseConfig.SUPABASE_PUBLIC_KEY)
                .addHeader("Content-Type", "application/json")
                .post(bodyJson.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val user = json.optJSONObject("user")
                val emailResult = user?.optString("email") ?: email
                val token = json.optString("access_token")
                AuthResult(isSuccess = true, userEmail = emailResult, accessToken = token)
            } else {
                val errorMsg = parseErrorMessage(responseBody, response.code)
                AuthResult(isSuccess = false, errorMessage = errorMsg)
            }
        } catch (e: Exception) {
            AuthResult(isSuccess = false, errorMessage = e.localizedMessage ?: "Network connection error")
        }
    }

    suspend fun signInWithPassword(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val url = "$authBaseUrl/token?grant_type=password"
            val bodyJson = JSONObject().apply {
                put("email", email)
                put("password", password)
            }.toString()

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", SupabaseConfig.SUPABASE_PUBLIC_KEY)
                .addHeader("Content-Type", "application/json")
                .post(bodyJson.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val user = json.optJSONObject("user")
                val emailResult = user?.optString("email") ?: email
                val token = json.optString("access_token")
                AuthResult(isSuccess = true, userEmail = emailResult, accessToken = token)
            } else {
                val errorMsg = parseErrorMessage(responseBody, response.code)
                AuthResult(isSuccess = false, errorMessage = errorMsg)
            }
        } catch (e: Exception) {
            AuthResult(isSuccess = false, errorMessage = e.localizedMessage ?: "Network connection error")
        }
    }

    private fun parseErrorMessage(responseBody: String, statusCode: Int): String {
        return try {
            val json = JSONObject(responseBody)
            json.optString("error_description").ifBlank {
                json.optString("msg").ifBlank {
                    json.optString("message").ifBlank {
                        "Authentication failed (HTTP $statusCode)"
                    }
                }
            }
        } catch (e: Exception) {
            "Authentication failed (HTTP $statusCode)"
        }
    }
}
