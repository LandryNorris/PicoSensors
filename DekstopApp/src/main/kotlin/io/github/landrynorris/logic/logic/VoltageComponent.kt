package io.github.landrynorris.logic.logic

import com.arkivanov.decompose.ComponentContext
import io.github.landrynorris.logic.linesFlow
import io.github.landrynorris.logic.sensors.VoltageSensor
import kotlinx.coroutines.flow.*

interface VoltageLogic {
    val state: StateFlow<VoltageState>
}

class VoltageComponent(sensor: VoltageSensor, onBack: () -> Unit, context: ComponentContext): VoltageLogic, DeviceScreenComponent(sensor, onBack, context) {
    override val state = MutableStateFlow(VoltageState(0.0))
    override fun beginPollingData() {
        sensor.port.linesFlow().onEach {
            val voltage = parseVoltageReading(it) ?: return@onEach
            state.update { state -> state.copy(currentVoltage = voltage) }
        }.launchIn(coroutineScope)
    }

    private fun parseVoltageReading(line: String): Double? {
        val afterColon = line.substringAfterLast(":")

        return afterColon.toDoubleOrNull()
    }
}

data class VoltageState(val currentVoltage: Double)
