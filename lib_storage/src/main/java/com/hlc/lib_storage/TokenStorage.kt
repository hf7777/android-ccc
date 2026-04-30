package com.hlc.lib_storage

import kotlinx.coroutines.flow.Flow

interface TokenStorage {
    suspend fun getToken(): String?
    fun getTokenFlow(): Flow<String?>
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}
