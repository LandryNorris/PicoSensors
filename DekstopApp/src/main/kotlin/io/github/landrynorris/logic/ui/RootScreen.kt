package io.github.landrynorris.logic.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.landrynorris.logic.logic.RootLogic
import io.github.landrynorris.logic.logic.SensorState

@Composable
fun RootScreen(logic: RootLogic) {
    val state by logic.state.collectAsState()

    Row {
        for(sensor in state.sensors) {
            Sensor(sensor)
        }
    }
}

@Composable
fun Sensor(sensorState: SensorState) {
    Card {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(sensorState.serialNumber)
            Text(sensorState.path)
        }
    }
}
