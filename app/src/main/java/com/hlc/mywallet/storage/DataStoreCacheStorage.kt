package com.hlc.mywallet.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.first
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 基于 DataStore 的通用缓存实现
 */
@Singleton
class DataStoreCacheStorage @Inject constructor(
    private val context: Context,
    private val moshi: Moshi
) : CacheStorage {

    override suspend fun <T> save(key: String, value: T) {
        if (value == null) return
        val nonNullValue = value as Any

        val type: Type = when (nonNullValue) {
            is List<*> -> {
                val elementType = nonNullValue.firstOrNull()?.javaClass ?: Any::class.java
                Types.newParameterizedType(List::class.java, elementType)
            }
            else -> nonNullValue::class.java
        }

        val adapter = moshi.adapter<Any>(type)
        val json = adapter.toJson(nonNullValue)
        val prefKey = stringPreferencesKey(key)

        context.appDataStore.edit { preferences ->
            preferences[prefKey] = json
        }
    }

    override suspend fun <T> get(key: String, clazz: Class<T>): T? {
        val prefKey = stringPreferencesKey(key)
        val preferences = context.appDataStore.data.first()
        val json = preferences[prefKey] ?: return null

        return runCatching {
            val adapter = moshi.adapter(clazz)
            adapter.fromJson(json)
        }.getOrNull()
    }

    override suspend fun <T> getList(key: String, elementClass: Class<T>): List<T>? {
        val prefKey = stringPreferencesKey(key)
        val preferences = context.appDataStore.data.first()
        val json = preferences[prefKey] ?: return null
        val type = Types.newParameterizedType(List::class.java, elementClass)

        return runCatching {
            val adapter = moshi.adapter<List<T>>(type)
            adapter.fromJson(json)
        }.getOrNull()
    }

    override suspend fun remove(key: String) {
        val prefKey = stringPreferencesKey(key)
        context.appDataStore.edit { preferences ->
            preferences.remove(prefKey)
        }
    }

    override suspend fun clear() {
        context.appDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

/**
 * 扩展函数：简化获取数据的调用
 */
inline fun <reified T> CacheStorage.getTyped(key: String): T? {
    return runCatching {
        kotlinx.coroutines.runBlocking {
            get(key, T::class.java)
        }
    }.getOrNull()
}
