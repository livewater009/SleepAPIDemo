package com.androidphotoapp.sleepapidemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.androidphotoapp.sleepapidemo.ui.theme.SleepAPIDemoTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var sleepManager: SleepRequestManager
    private var logging by mutableStateOf(false)
    private val sleepLogs = mutableStateListOf<String>()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sleepManager = SleepRequestManager(this)

        setContent {
            SleepAPIDemoTheme {
                MainScreenProfessional(
                    logging = logging,
                    sleepLogs = sleepLogs,
                    onToggleLogging = { toggleLogging() },
                    onSendMockData = { sendMockSleep() }
                )
            }
        }
    }

    // TODO: Review Activity Recognition permission checking.
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun activityRecognitionPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        );
    }

    // ✅ Permission launcher
    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestPermissionLauncher =  registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // User granted permission, now you can toggle logging again
            toggleLogging()
        } else {
            android.widget.Toast.makeText(
                this,
                "ACTIVITY_RECOGNITION permission denied",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun toggleLogging() {
        if (activityRecognitionPermissionApproved()) {
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            try {
                if (logging) {
                    sleepManager.unsubscribeFromSleepUpdates()
                    SensorTracker.stop()
                    sleepLogs.add("[${currentTime}] Stopped Sleep Updates")
                } else {
                    sleepManager.subscribeToSleepUpdates()
                    SensorTracker.start(this) { log ->
                        SleepDataRepository.addLogs(listOf(log))
                        sleepLogs.add(log)
                    }
                    sleepLogs.add("[${currentTime}] Started Sleep Updates")
                }
                logging = !logging
            } catch (e: SecurityException) {
                android.widget.Toast.makeText(
                    this,
                    "Missing ACTIVITY_RECOGNITION permission",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    private fun sendMockSleep() {
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        // Simulate sleep events
        val mockLogs = listOf(
            "[${currentTime}] Logging SleepSegmentEvents",
            "[${currentTime}] 00:30 to 00:60 with status IN_BED",
            "[${currentTime}] 00:60 to 01:00 with status ASLEEP",
            "[${currentTime}] Logging SleepClassifyEvents",
            "[${currentTime}] Confidence: 90 - Light: 0 - Motion: 0",
            "[${currentTime}] Confidence: 70 - Light: 1 - Motion: 1"
        )
        sleepLogs.addAll(mockLogs)
    }
}

// ---------------------------
// Helper function to detect emulator
// ---------------------------
fun isEmulator(): Boolean {
    val fingerprint = Build.FINGERPRINT.lowercase()
    val model = Build.MODEL.lowercase()
    val manufacturer = Build.MANUFACTURER.lowercase()
    val brand = Build.BRAND.lowercase()
    val device = Build.DEVICE.lowercase()
    val product = Build.PRODUCT.lowercase()

    return fingerprint.contains("generic") ||
            fingerprint.contains("emulator") ||
            model.contains("emulator") ||
            model.contains("sdk") ||
            manufacturer.contains("genymotion") ||
            brand.startsWith("generic") ||
            device.startsWith("generic") ||
            product.contains("sdk") ||
            product.contains("google_sdk") ||
            product.contains("emulator") ||
            product.contains("simulator")
}

// ---------------------------
// Composable Professional UI
// ---------------------------
@Composable
fun MainScreenProfessional(
    logging: Boolean,
    sleepLogs: List<String>,
    onToggleLogging: () -> Unit,
    onSendMockData: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            Text(
                text = "Sleep Tracker",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onToggleLogging() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (logging) "Stop Updates" else "Start Updates")
                }

                if (isEmulator()) {
                    Button(
                        onClick = { onSendMockData() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text("Send Mock", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sleep Logs",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sleepLogs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White)
                                .padding(12.dp)
                        ) {
                            Text(text = log)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreviewProfessional() {
    SleepAPIDemoTheme {
        MainScreenProfessional(
            logging = false,
            sleepLogs = listOf(
                "[12:30:00] Started Sleep Updates",
                "[12:35:00] Logging SleepSegmentEvents",
                "[12:36:00] Confidence: 90 - Light: 0 - Motion: 0"
            ),
            onToggleLogging = {},
            onSendMockData = {}
        )
    }
}
