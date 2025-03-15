package io.github.landrynorris.logic.sensors

import io.github.landrynorris.logic.PicoSerialDevice

class VoltageSensor(override val serialDevice: PicoSerialDevice): PicoSensor {
    val dataFlow = serialDevice.linesFlow

    override val readableName = "Voltage Sensor"
}