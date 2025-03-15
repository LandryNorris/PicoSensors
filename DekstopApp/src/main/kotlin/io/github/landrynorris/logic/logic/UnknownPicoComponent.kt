package io.github.landrynorris.logic.logic

import com.arkivanov.decompose.ComponentContext
import io.github.landrynorris.logic.sensors.UnknownPicoSensor

class UnknownPicoComponent(sensor: UnknownPicoSensor, onBack: () -> Unit, context: ComponentContext): DeviceScreenComponent(sensor, onBack, context) {
    override fun beginPollingData() {}
}
