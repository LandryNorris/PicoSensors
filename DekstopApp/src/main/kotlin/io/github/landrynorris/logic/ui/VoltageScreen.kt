package io.github.landrynorris.logic.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.landrynorris.logic.logic.VoltageLogic

@Composable
fun VoltageScreen(logic: VoltageLogic) {
    val state by logic.state.collectAsState()

    DisposableEffect(Unit) {
        logic.beginPollingData()

        onDispose {
            logic.stopPollingData()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voltage Sensor") },
                navigationIcon = {
                    IconButton(onClick = logic::backPessed) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            VoltageText(state.currentVoltage)
        }
    }
}

@Composable
fun VoltageText(voltage: Double) {
    Text("Voltage: ${voltage}V")
}
