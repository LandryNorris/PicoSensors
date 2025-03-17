/*
 * Copyright 2025 Landry Norris
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.landrynorris.logic.SensorDetector
import io.github.landrynorris.logic.SensorDetector.beginDetection
import io.github.landrynorris.logic.logic.RootComponent
import io.github.landrynorris.logic.ui.RootScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val APP_NAME = "Sensor Client"

fun main() = application {
    val lifecycle = LifecycleRegistry()

    val rootComponent = RootComponent(DefaultComponentContext(lifecycle))

    CoroutineScope(Dispatchers.IO).launch {
        SensorDetector.sensorFlow.collect {
            println("Sensor detected! $it")
        }
    }.invokeOnCompletion {
        println("Stopping listening")
    }
    beginDetection()
    rootComponent.beginDetectingDevices()
    Window(
        title = APP_NAME,
        onCloseRequest = ::exitApplication
    ) {
        RootScreen(rootComponent)
    }
}
