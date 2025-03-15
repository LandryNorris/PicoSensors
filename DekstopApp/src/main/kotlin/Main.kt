import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.landrynorris.logic.SensorDetector
import io.github.landrynorris.logic.SensorDetector.beginDetection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    CoroutineScope(Dispatchers.IO).launch {
        SensorDetector.sensorFlow.collect {
            println("Sensor detected! $it")
        }
    }.invokeOnCompletion {
        println("Stopping listening")
    }
    beginDetection()
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
