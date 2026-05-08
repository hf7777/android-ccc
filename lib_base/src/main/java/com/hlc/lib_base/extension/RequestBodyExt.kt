package com.hlc.lib_base.extension

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * 将 Map 转换为 JSON RequestBody
 */
fun Map<String, Any?>.toRequestBody(): RequestBody {
    val jsonObject = JSONObject()
    forEach { (key, value) ->
        jsonObject.put(key, value)
    }
    return jsonObject.toString()
        .toRequestBody("application/json; charset=utf-8".toMediaType())
}

/**
 * 将 Pair 列表转换为 JSON RequestBody
 */
fun List<Pair<String, Any?>>.toRequestBody(): RequestBody {
    val jsonObject = JSONObject()
    forEach { (key, value) ->
        jsonObject.put(key, value)
    }
    return jsonObject.toString()
        .toRequestBody("application/json; charset=utf-8".toMediaType())
}

/**
 * 构建 JSON RequestBody
 */
fun buildJsonBody(vararg params: Pair<String, Any?>): RequestBody {
    return params.toList().toRequestBody()
}
