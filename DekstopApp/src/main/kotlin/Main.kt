import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.landrynorris.logic.SensorDetector
import io.github.landrynorris.logic.SensorDetector.beginDetection
import io.github.landrynorris.logic.logic.RootComponent
import io.github.landrynorris.logic.ui.RootScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main() = application {
    val rootComponent = RootComponent()
    CoroutineScope(Dispatchers.IO).launch {
        SensorDetector.sensorFlow.collect {
            println("Sensor detected! $it")
        }
    }.invokeOnCompletion {
        println("Stopping listening")
    }
    beginDetection()
    rootComponent.beginDetectingDevices()
    Window(onCloseRequest = ::exitApplication) {
        RootScreen(rootComponent)
    }
}
