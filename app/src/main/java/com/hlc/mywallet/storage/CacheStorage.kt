package com.hlc.mywallet.storage

/**
 * 通用缓存存储接口
 * 支持任意类型数据的缓存
 */
interface CacheStorage {
    /**
     * 保存数据
     * @param key 缓存键
     * @param value 缓存值
     */
    suspend fun <T> save(key: String, value: T)
    
    /**
     * 获取数据
     * @param key 缓存键
     * @param clazz 数据类型
     * @return 缓存的数据，不存在返回 null
     */
    suspend fun <T> get(key: String, clazz: Class<T>): T?

    /**
     * 获取列表数据
     * @param key 缓存键
     * @param elementClass 列表元素类型
     * @return 缓存的列表数据，不存在返回 null
     */
    suspend fun <T> getList(key: String, elementClass: Class<T>): List<T>?
    
    /**
     * 删除数据
     * @param key 缓存键
     */
    suspend fun remove(key: String)
    
    /**
     * 清空所有缓存
     */
    suspend fun clear()
}

/**
 * 缓存键常量
 */
object CacheKeys {
    const val PRICE_INFO = "price_info"
    const val USER_INFO = "user_info"
    const val TEAM_STATISTICS = "team_statistics"
    const val MY_WALLET = "my_wallet"
}
