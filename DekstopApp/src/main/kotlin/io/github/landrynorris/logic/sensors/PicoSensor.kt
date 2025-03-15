package io.github.landrynorris.logic.sensors

import com.fazecast.jSerialComm.SerialPort
import io.github.landrynorris.logic.linesFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration

interface PicoSensor {
    val port: SerialPort
}

fun sensorFromLine(line: String, port: SerialPort): PicoSensor {
    return when(val label = line.substringBefore(":")) {
        "V" -> VoltageSensor(port)
        else -> error("Unknown sensor type: $label")
    }
}

suspend fun SerialPort.awaitKnownType(timeout: Duration): PicoSensor {
    val success = openPort()

    if(!success) {
        return UnknownPicoSensor(this)
    }

    val maybeSensor = withTimeoutOrNull(timeout) {
        linesFlow().first()
    }

    if(maybeSensor == null) {
        return UnknownPicoSensor(this)
    }

    return sensorFromLine(maybeSensor, this)
}
