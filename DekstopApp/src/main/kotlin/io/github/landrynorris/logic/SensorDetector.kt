package io.github.landrynorris.logic

import io.github.landrynorris.logic.sensors.PicoSensor
import io.github.landrynorris.logic.sensors.awaitKnownType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

object SensorDetector {
    private val mutableSensorFlow = MutableStateFlow<List<PicoSensor>>(listOf())
    private val backgroundScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val sensorFlow: StateFlow<List<PicoSensor>> = mutableSensorFlow

    fun beginDetection() {
        backgroundScope.launch {
            runDetection()
        }
    }

    private suspend fun runDetection() {
        UsbDeviceDetector.devices.collect { devices ->
            mutableSensorFlow.update { sensors ->
                val remainingSensors = sensors.filter { sensor ->
                    devices.any { device ->
                        sensor.port.serialNumber == device.serialNumber
                    }
                }

                val newSensors = devices.filter { device ->
                    sensors.none { sensor ->
                        sensor.port.serialNumber == device.serialNumber
                    }
                }.map { it.awaitKnownType(200.milliseconds) }

                remainingSensors + newSensors
            }
        }
    }
}