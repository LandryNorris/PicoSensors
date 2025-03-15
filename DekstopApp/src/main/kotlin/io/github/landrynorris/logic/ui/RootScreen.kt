package io.github.landrynorris.logic.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
            Sensor(sensor, onClick = { selectDevice(sensor.serialNumber) })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Sensor(sensorState: SensorState, onClick: () -> Unit) {
    Card(onClick = onClick) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(sensorState.serialNumber)
            Text(sensorState.path)
        }
    }
}
