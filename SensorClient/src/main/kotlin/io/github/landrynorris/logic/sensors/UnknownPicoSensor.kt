package io.github.landrynorris.logic.sensors

import io.github.landrynorris.logic.PicoSerialDevice

class UnknownPicoSensor(override val serialDevice: PicoSerialDevice): PicoSensor {
    override val readableName = "Unknown Sensor"
}