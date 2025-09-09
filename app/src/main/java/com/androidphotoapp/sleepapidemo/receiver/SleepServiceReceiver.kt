package com.androidphotoapp.sleepapidemo.receiver

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.SleepClassifyEvent
import com.google.android.gms.location.SleepSegmentEvent
import com.androidphotoapp.sleepapidemo.SleepDataRepository

class SleepServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("SleepServiceReceiver", "Get Data!!!")
        
        if (intent == null) return // ignore null intent

        // --- Simulated test event ---
        if (intent.action == "com.androidphotoapp.sleepapidemo.SIMULATE_SLEEP") {
            Log.d("SleepServiceReceiver", "Simulated sleep event received!")
            Toast.makeText(context, "Simulated sleep event received!", Toast.LENGTH_SHORT).show()
            return
        }

        val logs = mutableListOf<String>()

        // ---- SleepSegmentEvent ----
        if (SleepSegmentEvent.hasEvents(intent)) {
            val events = SleepSegmentEvent.extractEvents(intent)
                .filter { it.status != SleepSegmentEvent.STATUS_NOT_DETECTED }

            if (events.isNotEmpty()) {
                logs.add("Sleep Segments:")

                // Log each segment
                for (event in events) {
                    val startTime = millisToTimeString(event.startTimeMillis)
                    val endTime = millisToTimeString(event.endTimeMillis)
                    val statusString = when (event.status) {
                        SleepSegmentEvent.STATUS_SUCCESSFUL -> "In Bed"
                        SleepSegmentEvent.STATUS_MISSING_DATA -> "Partial Sleep Data"
                        else -> "Unknown"
                    }
                    logs.add("$startTime to $endTime: $statusString")
                }

                // Sleep start and wake-up time
                val sleepStart = millisToTimeString(events.minOf { it.startTimeMillis })
                val wakeUpTime = millisToTimeString(events.maxOf { it.endTimeMillis })
                logs.add("Sleep Start: $sleepStart")
                logs.add("Wake Up Time: $wakeUpTime")
            }
        }

        // ---- SleepClassifyEvent ----
        if (SleepClassifyEvent.hasEvents(intent)) {
            val classifyEvents = SleepClassifyEvent.extractEvents(intent)
            logs.add("Sleep Classify Events:")
            for (event in classifyEvents) {
                logs.add("Confidence: ${event.confidence} - Light: ${event.light} - Motion: ${event.motion}")
            }
        }

        // Update repository
        SleepDataRepository.addLogs(logs)
    }

    @SuppressLint("DefaultLocale")
    private fun millisToTimeString(timeMillis: Long): String {
        val calendar = java.util.Calendar.getInstance().apply { timeInMillis = timeMillis }
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute)
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
