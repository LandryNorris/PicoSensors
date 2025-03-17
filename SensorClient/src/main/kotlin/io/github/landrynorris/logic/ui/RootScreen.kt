package io.github.landrynorris.logic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.landrynorris.logic.logic.RootLogic
import io.github.landrynorris.logic.logic.RootState
import io.github.landrynorris.logic.logic.SensorState
import io.github.landrynorris.logic.logic.VoltageComponent

@Composable
fun RootScreen(logic: RootLogic) {
    val state by logic.state.collectAsState()
    val childState = logic.child.subscribeAsState()

    val activeChild = childState.value.child

    if(activeChild == null) {
        SelectDeviceScreen(state, logic::selectDevice)
    } else {
        when(val component = activeChild.instance) {
            is VoltageComponent -> VoltageScreen(component)
        }
    }
}

@Composable
fun SelectDeviceScreen(state: RootState, selectDevice: (String) -> Unit) {
    Row {
        for(sensor in state.sensors) {
            SensorCard(sensor, onClick = { selectDevice(sensor.serialNumber) })
        }
    }
}

@Composable
fun SensorCard(sensorState: SensorState, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .clickable(onClick = onClick)
            .background(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = sensorState.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = "S/N: ${sensorState.serialNumber}", style = MaterialTheme.typography.labelSmall)
            Text(text = "Path: ${sensorState.path}", style = MaterialTheme.typography.labelSmall)
        }
    }
}
