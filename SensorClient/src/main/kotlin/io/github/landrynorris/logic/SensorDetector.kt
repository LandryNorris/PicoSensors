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
                val remainingSensors = sensors.filter { it.serialDevice.isOpen }

                val newSensors = devices.filter { device ->
                    sensors.none { sensor ->
                        sensor.serialDevice.serialNumber == device.serialNumber
                    }
                }.map {
                    it.awaitKnownType(200.milliseconds)
                }

                remainingSensors + newSensors
            }
        }
    }
}