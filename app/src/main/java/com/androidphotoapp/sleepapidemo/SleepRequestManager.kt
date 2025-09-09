package com.androidphotoapp.sleepapidemo

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.androidphotoapp.sleepapidemo.receiver.SleepServiceReceiver
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest

class SleepRequestManager(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun isSensorAvailable(sensorType: Int): Boolean {
        val sensor = sensorManager.getDefaultSensor(sensorType)
        return sensor != null
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun subscribeToSleepUpdates() {
        val accelerometerAvailable = isSensorAvailable(Sensor.TYPE_ACCELEROMETER)
        val gyroscopeAvailable = isSensorAvailable(Sensor.TYPE_GYROSCOPE)
        val lightSensorAvailable = isSensorAvailable(Sensor.TYPE_LIGHT)

        Log.d("SensorCheck", "Accelerometer: $accelerometerAvailable")
        Log.d("SensorCheck", "Gyroscope: $gyroscopeAvailable")
        Log.d("SensorCheck", "Light: $lightSensorAvailable")

        // TODO: Request Sleep API updates
        val task = ActivityRecognition.getClient(context)
            .requestSleepSegmentUpdates(
                SleepServiceReceiver.createPendingIntent(context),
                SleepSegmentRequest.getDefaultSleepSegmentRequest()
            )
        task.addOnSuccessListener {
            Toast.makeText(context, "Successfully subscribed to sleep data.", Toast.LENGTH_SHORT).show()
        }
        task.addOnFailureListener { exception ->
            Toast.makeText(context, "Exception when subscribing to sleep data: $exception", Toast.LENGTH_SHORT).show()
        }
    }

    fun unsubscribeFromSleepUpdates() {
        ActivityRecognition.getClient(context)
            .removeSleepSegmentUpdates(SleepServiceReceiver.createPendingIntent(context))
        Toast.makeText(context, "Unsubscribed from sleep updates", Toast.LENGTH_SHORT).show()
    }
}
