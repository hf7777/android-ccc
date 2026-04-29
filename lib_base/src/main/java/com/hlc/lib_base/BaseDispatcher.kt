package com.hlc.lib_base

import kotlinx.coroutines.CoroutineDispatcher

interface BaseDispatcher {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
}
