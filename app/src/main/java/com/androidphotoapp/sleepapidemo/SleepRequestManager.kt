package com.androidphotoapp.sleepapidemo

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.androidphotoapp.sleepapidemo.receiver.SleepServiceReceiver
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SleepRequestManager(private val context: Context) {

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun subscribeToSleepUpdates() {
        ActivityRecognition.getClient(context)
            .requestSleepSegmentUpdates(
                SleepServiceReceiver.createPendingIntent(context),
                SleepSegmentRequest.getDefaultSleepSegmentRequest()
            )

        // Ensure Toast runs on main thread
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "Started Sleep Updates", Toast.LENGTH_LONG).show()
        }
    }

    fun unsubscribeFromSleepUpdates() {
        ActivityRecognition.getClient(context)
            .removeSleepSegmentUpdates(SleepServiceReceiver.createPendingIntent(context))

        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "Stopped Sleep Updates", Toast.LENGTH_LONG).show()
        }
    }
}
