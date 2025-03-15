package io.github.landrynorris.logic.logic

import com.arkivanov.decompose.ComponentContext
import io.github.landrynorris.logic.sensors.PicoSensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface DeviceScreenLogic {
    fun beginPollingData()
    fun stopPollingData()
}

// TODO(Landry): Convert to a navigation library, such as Decompose
abstract class DeviceScreenComponent(
    val sensor: PicoSensor,
    val onBack: () -> Unit,
    context: ComponentContext
): ComponentContext by context, DeviceScreenLogic {
    val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val serialNumber get() = sensor.serialDevice.serialNumber
}
