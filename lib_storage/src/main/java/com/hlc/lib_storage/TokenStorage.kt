package com.hlc.lib_storage

interface TokenStorage {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
}
