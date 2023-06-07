package com.noukta.wallpaper.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object IoScope {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun launch(query: () -> Unit) {
        coroutineScope.launch {
            query()
        }
    }
}