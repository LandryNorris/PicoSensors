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
package io.github.landrynorris.logic.sensors

import io.github.landrynorris.logic.PicoSerialDevice
import kotlinx.coroutines.flow.mapNotNull

class VoltageSensor(override val serialDevice: PicoSerialDevice): PicoSensor {
    val dataFlow = serialDevice.linesFlow.mapNotNull { line ->
        val parts = line.split(":")

        if(parts.size != 2) return@mapNotNull null

        parts[1].toDoubleOrNull()
    }

    override val readableName = "Voltage Sensor"
}