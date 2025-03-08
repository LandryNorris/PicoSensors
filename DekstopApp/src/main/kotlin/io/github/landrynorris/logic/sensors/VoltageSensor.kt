package io.github.landrynorris.logic.sensors

import com.fazecast.jSerialComm.SerialPort
import io.github.landrynorris.logic.linesFlow

class VoltageSensor(override val port: SerialPort): PicoSensor {
    val dataFlow = port.linesFlow()
}