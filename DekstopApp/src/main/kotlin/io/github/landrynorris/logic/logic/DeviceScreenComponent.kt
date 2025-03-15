package io.github.landrynorris.logic.logic

import com.arkivanov.decompose.ComponentContext
import io.github.landrynorris.logic.sensors.PicoSensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

// TODO(Landry): Convert to a navigation library, such as Decompose
abstract class DeviceScreenComponent(
    val sensor: PicoSensor,
    val onBack: () -> Unit,
    context: ComponentContext
): ComponentContext by context {
    val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val serialNumber get() = sensor.port.serialNumber

    abstract fun beginPollingData()
}
