package com.androidphotoapp.sleepapidemo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

object SensorTracker {

  private var sensorManager: SensorManager? = null

  // Latest sensor values
  var avgSensorMovement: Float = 0f
    private set
  var currentLight: Float = 0f
    private set
  var gyroX: Float = 0f
    private set
  var gyroY: Float = 0f
    private set
  var gyroZ: Float = 0f
    private set

  // Sleep state
  var isSleeping: Boolean = false
    private set

  private var logCallback: ((String) -> Unit)? = null

  private val sensorListener = object : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent) {
      when (event.sensor.type) {
        Sensor.TYPE_ACCELEROMETER -> {
          val x = event.values[0]
          val y = event.values[1]
          val z = event.values[2]
          avgSensorMovement = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        }
        Sensor.TYPE_LIGHT -> {
          currentLight = event.values[0]
        }
        Sensor.TYPE_GYROSCOPE -> {
          gyroX = event.values[0]
          gyroY = event.values[1]
          gyroZ = event.values[2]
        }
      }
      detectSleepState()
      logSensors()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
  }

  fun start(context: Context, logCallback: (String) -> Unit) {
    if (sensorManager == null) {
      sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
      sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
        sensorManager?.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
      }
      sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)?.let {
        sensorManager?.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
      }
      sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let {
        sensorManager?.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
      }
      this.logCallback = logCallback
    }
  }

  fun stop() {
    sensorManager?.unregisterListener(sensorListener)
    sensorManager = null
    logCallback = null
  }

  private fun detectSleepState(motionThreshold: Float = 0.05f, lightThreshold: Float = 10f) {
    val sleepingNow = avgSensorMovement < motionThreshold && currentLight < lightThreshold
    if (sleepingNow != isSleeping) {
      isSleeping = sleepingNow
      logCallback?.invoke("SensorTracker → User is now ${if (isSleeping) "Sleeping" else "Awake"}")
    }
  }

  private fun logSensors() {
    val log = "SensorTracker → Motion: $avgSensorMovement, Light: $currentLight, Gyro: x=$gyroX y=$gyroY z=$gyroZ"
    logCallback?.invoke(log)
  }
}
