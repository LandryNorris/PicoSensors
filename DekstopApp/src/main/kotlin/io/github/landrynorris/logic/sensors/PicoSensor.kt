package io.github.landrynorris.logic.sensors

import com.fazecast.jSerialComm.SerialPort

interface PicoSensor {
    val port: SerialPort
}