package com.androidphotoapp.sleepapidemo

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.androidphotoapp.sleepapidemo.receiver.SleepServiceReceiver
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest

class SleepRequestManager(private val context: Context) {

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun subscribeToSleepUpdates() {
        ActivityRecognition.getClient(context)
            .requestSleepSegmentUpdates(
                SleepServiceReceiver.createPendingIntent(context),
                SleepSegmentRequest.getDefaultSleepSegmentRequest()
            )
        Toast.makeText(context, "Subscribed to sleep updates", Toast.LENGTH_SHORT).show()
    }

    fun unsubscribeFromSleepUpdates() {
        ActivityRecognition.getClient(context)
            .removeSleepSegmentUpdates(SleepServiceReceiver.createPendingIntent(context))
        Toast.makeText(context, "Unsubscribed from sleep updates", Toast.LENGTH_SHORT).show()
    }
}
