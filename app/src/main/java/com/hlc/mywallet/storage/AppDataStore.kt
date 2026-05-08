package com.hlc.mywallet.storage

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

internal val Context.appDataStore by preferencesDataStore(name = "app_storage")
