package io.github.landrynorris.logic.logic

import io.github.landrynorris.logic.sensors.UnknownPicoSensor

class UnknownPicoComponent(sensor: UnknownPicoSensor): DeviceScreenComponent(sensor) {
    override fun beginPollingData() {}
}
