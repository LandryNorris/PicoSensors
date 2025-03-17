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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration

interface PicoSensor {
    val serialDevice: PicoSerialDevice

    val readableName: String
}

fun sensorFromLine(line: String, port: PicoSerialDevice): PicoSensor {
    return when(val label = line.substringBefore(":")) {
        "V" -> VoltageSensor(port)
        else -> error("Unknown sensor type: $label")
    }
}

suspend fun PicoSerialDevice.awaitKnownType(timeout: Duration): PicoSensor {
    val maybeSensor = withTimeoutOrNull(timeout) {
        linesFlow.first()
    }

    if(maybeSensor == null) {
        return UnknownPicoSensor(this)
    }

    return sensorFromLine(maybeSensor, this)
}
