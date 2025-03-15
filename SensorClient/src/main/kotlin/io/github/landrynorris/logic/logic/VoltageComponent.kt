package io.github.landrynorris.logic.logic

import com.arkivanov.decompose.ComponentContext
import io.github.landrynorris.logic.sensors.VoltageSensor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.xlab
import org.jetbrains.letsPlot.label.ylab
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.ylim

const val NUM_READINGS_STORED = 5000

interface VoltageLogic: DeviceScreenLogic {
    val state: StateFlow<VoltageState>
}

class VoltageComponent(sensor: VoltageSensor, onBack: () -> Unit, context: ComponentContext): VoltageLogic, DeviceScreenComponent(sensor, onBack, context) {
    override val state = MutableStateFlow(VoltageState(0.0, listOf(), createFigure()))
    private var dataCollectionJob: Job? = null

    private val voltageSensor get() = sensor as VoltageSensor

    override fun beginPollingData() {
        dataCollectionJob = voltageSensor.dataFlow.onEach { voltage ->
            state.update { state -> state.copy(currentVoltage = voltage) }
            submitReading(voltage)
        }.launchIn(coroutineScope)
    }

    override fun stopPollingData() {
        dataCollectionJob?.cancel()
        dataCollectionJob = null
    }

    private fun parseVoltageReading(line: String): Double? {
        val afterColon = line.substringAfterLast(":")

        return afterColon.toDoubleOrNull()
    }

    private fun submitReading(value: Double) {
        state.update {
            val timestamp = Clock.System.now()
            val newValue = VoltageReading(value, timestamp)

            var newReadings = it.readings + newValue
            if(newReadings.size > NUM_READINGS_STORED) {
                newReadings = newReadings.drop(1)
            }

            val figure = createFigure() + geomLine(
                data = mapOf(
                    "voltage" to newReadings.map { data -> data.value },
                    "timestamp" to newReadings.map { data -> data.timestamp }
                )
            ) { x = "timestamp"; y = "voltage" }
            it.copy(readings = newReadings, figure = figure)
        }
    }

    private fun createFigure(): Plot {
        var p = letsPlot() + geomPoint()

        p += xlab("Timestamp") + ylab("Voltage (V)") + ylim(listOf(0, 3.3))
        return p
    }
}

data class VoltageReading(val value: Double, val timestamp: Instant)

data class VoltageState(val currentVoltage: Double, val readings: List<VoltageReading>, val figure: Plot)
