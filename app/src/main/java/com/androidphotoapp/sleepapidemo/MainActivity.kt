package com.androidphotoapp.sleepapidemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.androidphotoapp.sleepapidemo.ui.theme.SleepAPIDemoTheme

class MainActivity : ComponentActivity() {

    private lateinit var sleepManager: SleepRequestManager
    private var logging by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sleepManager = SleepRequestManager(this)

        setContent {
            SleepAPIDemoTheme {
                MainScreen(logging = logging,
                    onToggleLogging = { toggleLogging() })
            }
        }
    }

    private fun toggleLogging() {
        try {
            if (logging) {
                sleepManager.unsubscribeFromSleepUpdates()
            } else {
                sleepManager.subscribeToSleepUpdates()
            }
            logging = !logging
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "Missing ACTIVITY_RECOGNITION permission",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

@Composable
fun MainScreen(logging: Boolean, onToggleLogging: () -> Unit) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top
        ) {
            Button(
                onClick = { onToggleLogging() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (logging) "Stop Logging" else "Start Logging")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (logging) "Logging sleep updates..." else "Not logging",
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SleepAPIDemoTheme {
        MainScreen(logging = false, onToggleLogging = {})
    }
}
