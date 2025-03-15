package io.github.landrynorris.logic.sensors

import com.fazecast.jSerialComm.SerialPort

class UnknownPicoSensor(override val port: SerialPort): PicoSensor