package com.hlc.mywallet.common

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
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
    data object OrderInrListRefreshRequested : AppEvent
    /** 我的页用户统计等数据刷新 */
    data object MineRefreshRequested : AppEvent

    /** 新手任务概览更新，用于首页/我的页余额区展示切换 */
    data class NewbieSummaryUpdated(
        val isCompleted: Boolean,
        val totalReward: Double
    ) : AppEvent
}

/**
 * EventBus 内部使用的事件包装体。
 *
 * - key: 事件类型标识，用于按类型过滤
 * - version: 同类型事件的递增版本号，用于订阅方去重
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
 * 3. 多个页面可同时订阅同一事件（广播语义）
 * 4. 页面重新进入时，新订阅方可接住最近一次 replay 事件
 * 5. 同一订阅方重复 collect 时，不重复处理同版本事件
 */
object AppEventBus {

    private val eventVersions = ConcurrentHashMap<String, AtomicLong>()

    private val events = MutableSharedFlow<EventEnvelope>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun post(event: AppEvent) {
        val key = event::class.java.name
        val version = eventVersions.getOrPut(key) { AtomicLong(0L) }.incrementAndGet()
        events.tryEmit(EventEnvelope(key = key, version = version, event = event))
    }

    /**
     * 按事件类型订阅。每个 collect 独立维护已消费版本，互不影响。
     */
    fun <T : AppEvent> flow(eventClass: Class<T>): Flow<T> {
        val key = eventClass.name
        return flow {
            var lastHandledVersion = 0L

            suspend fun emitIfNew(envelope: EventEnvelope) {
                if (envelope.key != key || envelope.version <= lastHandledVersion) return
                lastHandledVersion = envelope.version
                emit(eventClass.cast(envelope.event)!!)
            }

            events.replayCache.forEach { emitIfNew(it) }
            events.collect { emitIfNew(it) }
        }
    }

    inline fun <reified T : AppEvent> flow(): Flow<T> = flow(T::class.java)
}
