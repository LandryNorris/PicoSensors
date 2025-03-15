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
