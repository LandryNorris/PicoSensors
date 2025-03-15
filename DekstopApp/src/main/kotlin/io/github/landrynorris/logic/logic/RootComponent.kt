package io.github.landrynorris.logic.logic

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
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
import kotlinx.serialization.Serializable

interface RootLogic {
    val child: Value<ChildSlot<SensorConfig, DeviceScreenComponent>>
    val state: StateFlow<RootState>
    fun beginDetectingDevices()
    fun selectDevice(serialNumber: String)
}

class RootComponent(context: ComponentContext): ComponentContext by context, RootLogic {
    override val state: MutableStateFlow<RootState> = MutableStateFlow(RootState())
    private val componentDetectionScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val navigation = SlotNavigation<SensorConfig>()

    override val child = childSlot(
        source = navigation,
        serializer = SensorConfig.serializer(),
        handleBackButton = true,
        childFactory = { config, component ->
            val sensor = getSensorBySerial(config.serialNumber)
            sensor ?: error("No sensor found with serial number: ${config.serialNumber}")

            createComponentForSensor(sensor, component)
        }
    )

    override fun beginDetectingDevices() {
        componentDetectionScope.launch {
            SensorDetector.sensorFlow.collect { sensors ->
                state.update { state ->
                    state.copy(
                        sensors = sensors.map { it.toSensorState() }
                    )
                }
            }
        }
    }

    override fun selectDevice(serialNumber: String) {
        val sensor = getSensorBySerial(serialNumber)
        sensor?.let {
            val config = createConfigFromSensor(it)
            navigation.activate(config)
        }
    }

    private fun onScreenBack() {
        navigation.dismiss()
    }

    private fun createComponentForSensor(sensor: PicoSensor, componentContext: ComponentContext): DeviceScreenComponent {
        return when(sensor) {
            is VoltageSensor -> VoltageComponent(sensor, ::onScreenBack, componentContext)
            is UnknownPicoSensor -> UnknownPicoComponent(sensor, ::onScreenBack, componentContext)
            else -> error("No component defined for sensor $sensor")
        }
    }

    private fun createConfigFromSensor(sensor: PicoSensor): SensorConfig {
        return when(sensor) {
            is VoltageSensor -> SensorConfig.VoltageSensorConfig(sensor.serialDevice.serialNumber)
            is UnknownPicoSensor -> SensorConfig.UnknpwnPicoSensorConfig(sensor.serialDevice.serialNumber)
            else -> error("No config type found for sensor: $sensor")
        }
    }

    private fun getSensorBySerial(serialNumber: String): PicoSensor? {
        return SensorDetector.sensorFlow.value.firstOrNull { it.serialDevice.serialNumber == serialNumber }
    }
}

@Serializable
sealed interface SensorConfig {
    val serialNumber: String

    @Serializable
    data class VoltageSensorConfig(override val serialNumber: String): SensorConfig

    @Serializable
    data class UnknpwnPicoSensorConfig(override val serialNumber: String): SensorConfig
}

fun PicoSensor.toSensorState(): SensorState {
    return SensorState(serialDevice.serialNumber, serialDevice.systemPortPath)
}

data class SensorState(val serialNumber: String, val path: String)

data class RootState(
    val activeComponent: DeviceScreenComponent? = null,
    val sensors: List<SensorState> = listOf()
)
