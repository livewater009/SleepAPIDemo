package com.androidphotoapp.sleepapidemo.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.androidphotoapp.sleepapidemo.SleepRequestManager

class BootReceiver : BroadcastReceiver() {
  @RequiresApi(Build.VERSION_CODES.Q)
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
      Log.d("BootReceiver", "Device rebooted → trying to re-subscribe to Sleep API")

      val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACTIVITY_RECOGNITION
      ) == PackageManager.PERMISSION_GRANTED

      if (hasPermission) {
        try {
          val sleepManager = SleepRequestManager(context)
          sleepManager.subscribeToSleepUpdates()
          Toast.makeText(context, "Re-subscribed to Sleep API after reboot", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
          Log.e("BootReceiver", "Failed to subscribe after reboot: ${e.message}")
        }
      } else {
        Log.w("BootReceiver", "ACTIVITY_RECOGNITION permission not granted → skipping subscription")
      }
    }
  }
}
