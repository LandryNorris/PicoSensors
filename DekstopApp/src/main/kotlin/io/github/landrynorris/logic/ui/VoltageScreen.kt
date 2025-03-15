package io.github.landrynorris.logic.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.landrynorris.logic.logic.VoltageLogic

@Composable
fun VoltageScreen(logic: VoltageLogic) {
    val state by logic.state.collectAsState()

    Column {
        VoltageText(state.currentVoltage)
    }
}

@Composable
fun VoltageText(voltage: Double) {
    Text("Voltage: ${voltage}V")
}
