package com.androidphotoapp.sleepapidemo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

object SleepDataRepository {
    private val _sleepLogs = MutableStateFlow<List<String>>(emptyList())
    val sleepLogs = _sleepLogs.asStateFlow()

    fun addLogs(logs: List<String>) {
        _sleepLogs.value = logs
    }

    fun addStatusLog(status: String) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val time = sdf.format(Date())
        val updatedLogs = _sleepLogs.value.toMutableList()
        updatedLogs.add("$status - $time")
        _sleepLogs.value = updatedLogs
    }
}
