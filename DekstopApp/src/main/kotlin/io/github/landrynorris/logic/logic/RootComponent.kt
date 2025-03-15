package io.github.landrynorris.logic.logic

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import io.github.landrynorris.logic.SensorDetector
import io.github.landrynorris.logic.sensors.PicoSensor
import io.github.landrynorris.logic.sensors.UnknownPicoSensor
import io.github.landrynorris.logic.sensors.VoltageSensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface RootLogic {
    val state: StateFlow<RootState>
    fun beginDetectingDevices()
    fun selectDevice(serialNumber: String)
}

class RootComponent(context: ComponentContext): ComponentContext by context, RootLogic {
    override val state: MutableStateFlow<RootState> = MutableStateFlow(RootState())
    private val componentDetectionScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun beginDetectingDevices() {
        componentDetectionScope.launch {
            SensorDetector.sensorFlow.collect { sensors ->
                state.update { state ->
                    val remainingComponents = state.currentComponents.filter {
                        sensors.any { sensor -> sensor.port.serialNumber == it.serialNumber }
                    }
                    val newComponents = sensors.filter { sensor ->
                        state.currentComponents.none { it.serialNumber == sensor.port.serialNumber }
                    }.map {
                        createComponentForSensor(sensor = it)
                    }

                    state.copy(
                        currentComponents = remainingComponents + newComponents,
                        sensors = sensors.map { it.toSensorState() }
                    )
                }
            }
        }
    }

    override fun selectDevice(serialNumber: String) {
        state.update {
            val component = it.currentComponents.firstOrNull { component -> component.serialNumber == serialNumber }

            it.copy(activeComponent = component)
        }
    }

    private fun onScreenBack() {
        state.update { it.copy(activeComponent = null) }
    }

    private fun createComponentForSensor(sensor: PicoSensor): DeviceScreenComponent {
        return when(sensor) {
            is VoltageSensor -> VoltageComponent(sensor, ::onScreenBack, childContext("V ${sensor.port.serialNumber}"))
            is UnknownPicoSensor -> UnknownPicoComponent(sensor, ::onScreenBack, childContext("U ${sensor.port.serialNumber}"))
            else -> error("No component defined for sensor $sensor")
        }
    }
}

fun PicoSensor.toSensorState(): SensorState {
    return SensorState(port.serialNumber, port.systemPortPath)
}

data class SensorState(val serialNumber: String, val path: String)

data class RootState(
    val currentComponents: List<DeviceScreenComponent> = listOf(),
    val activeComponent: DeviceScreenComponent? = null,
    val sensors: List<SensorState> = listOf()
)
