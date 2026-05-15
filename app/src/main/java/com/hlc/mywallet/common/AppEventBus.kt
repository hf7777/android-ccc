package com.hlc.mywallet.common

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * 应用内跨页面/跨模块事件定义。
 *
 * 这里承载的是“轻量广播型”事件，而不是带复杂状态的数据源。
 * 如果后续某个能力需要持续状态同步，优先考虑 ViewModel/Repository 持有状态，
 * 不要把 EventBus 用成状态仓库。
 */
sealed interface AppEvent {
    data object WalletRefreshRequested : AppEvent
}

/**
 * EventBus 内部使用的事件包装体。
 *
 * - key: 事件类型标识，用于按类型过滤
 * - version: 同类型事件的递增版本号，用于避免 replay 场景下重复消费
 * - event: 真实业务事件
 */
private data class EventEnvelope(
    val key: String,
    val version: Long,
    val event: AppEvent
)

/**
 * 基于 SharedFlow 的轻量应用事件总线。
 *
 * 设计目标：
 * 1. 提供类似 EventBus 的跨页面消息传递能力
 * 2. 支持按事件类型订阅，业务侧调用尽量简单
 * 3. 在页面短暂不可见后重新进入时，仍能接住最近一次事件
 * 4. 避免 SharedFlow replay 导致同一事件被重复消费
 *
 * 当前实现要点：
 * - 使用 replay = 1，保证最新一条事件可被后续恢复的页面接收到
 * - 为每种事件类型维护独立 version
 * - 订阅时记录已消费 version，避免重复处理同一条 replay 事件
 *
 * 适用场景：
 * - 页面关闭后通知上层列表刷新
 * - 跨模块的轻量 UI 事件广播
 *
 * 不适用场景：
 * - 长期持有状态
 * - 需要严格投递给单一接收者的消息
 * - 高可靠消息队列语义
 */
object AppEventBus {

    /**
     * 每种事件类型各自维护一个递增版本号。
     * 发布事件时递增，用来标识“这是该类型的第几次事件”。
     */
    private val eventVersions = ConcurrentHashMap<String, AtomicLong>()

    /**
     * 每种事件类型最近一次已消费的版本号。
     * 主要用于处理 SharedFlow replay = 1 带来的重复消费问题。
     */
    private val handledVersions = ConcurrentHashMap<String, AtomicLong>()

    /**
     * 全局事件流。
     *
     * replay = 1:
     * 保留最近一条事件，确保页面从后台返回或重新进入 STARTED 状态时，
     * 还有机会拿到最近一次广播。
     *
     * extraBufferCapacity = 1:
     * 给 tryEmit 留一点缓冲，减少主线程瞬时发送失败的概率。
     */
    private val events = MutableSharedFlow<EventEnvelope>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * 发送一个应用事件。
     *
     * 业务侧统一通过这个入口发送，避免为每个事件单独暴露 sendXxx 方法。
     */
    fun post(event: AppEvent) {
        val key = event::class.java.name
        val version = eventVersions.getOrPut(key) { AtomicLong(0L) }.incrementAndGet()
        events.tryEmit(EventEnvelope(key = key, version = version, event = event))
    }

    /**
     * 按事件类型订阅。
     *
     * 返回的 Flow 只会发出指定类型的事件，并自动做重复消费拦截。
     * 适合在 Fragment/Activity 中配合 repeatOnLifecycle 使用。
     */
    fun <T : AppEvent> flow(eventClass: Class<T>): Flow<T> {
        val key = eventClass.name
        return events
            .filter { envelope ->
                envelope.key == key && shouldConsume(key, envelope.version)
            }
            .map { envelope -> eventClass.cast(envelope.event)!! }
    }

    /**
     * Kotlin 友好的泛型订阅入口。
     *
     * 用法：
     * AppEventBus.flow<AppEvent.WalletRefreshRequested>().collect { ... }
     */
    inline fun <reified T : AppEvent> flow(): Flow<T> {
        return flow(T::class.java)
    }

    /**
     * 判定某个事件版本是否应该被当前订阅方消费。
     *
     * 由于 SharedFlow 配置了 replay = 1，页面重新订阅时可能再次拿到上一条事件。
     * 这里通过“同类型事件版本号去重”保证一条事件只被消费一次。
     */
    private fun shouldConsume(key: String, version: Long): Boolean {
        val handledVersion = handledVersions.getOrPut(key) { AtomicLong(0L) }
        while (true) {
            val current = handledVersion.get()
            if (version <= current) {
                return false
            }
            if (handledVersion.compareAndSet(current, version)) {
                return true
            }
        }
    }
}
