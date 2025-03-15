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