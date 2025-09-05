package com.androidphotoapp.sleepapidemo.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.location.SleepClassifyEvent
import com.google.android.gms.location.SleepSegmentEvent
import com.androidphotoapp.sleepapidemo.SleepDataRepository

class SleepServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return // ignore null intent
        val logs = mutableListOf<String>()

        if (SleepSegmentEvent.hasEvents(intent)) {
            val events = SleepSegmentEvent.extractEvents(intent)
            logs.add("Logging SleepSegmentEvents")
            for (event in events) {
                val startSeconds = event.startTimeMillis / 1000 % 60
                val endSeconds = event.endTimeMillis / 1000 % 60
                logs.add("$startSeconds to $endSeconds with status ${event.status}")
            }
        }

        if (SleepClassifyEvent.hasEvents(intent)) {
            val events = SleepClassifyEvent.extractEvents(intent)
            logs.add("Logging SleepClassifyEvents")
            for (event in events) {
                logs.add("Confidence: ${event.confidence} - Light: ${event.light} - Motion: ${event.motion}")
            }
        }

        // Update repository
        SleepDataRepository.addLogs(logs)
    }

    companion object {
        fun createPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, SleepServiceReceiver::class.java)
            val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            return PendingIntent.getBroadcast(context, 0, intent, flag)
        }
    }
}
