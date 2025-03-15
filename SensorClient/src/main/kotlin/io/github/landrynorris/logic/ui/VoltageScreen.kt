package io.github.landrynorris.logic.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.landrynorris.logic.logic.VoltageLogic
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.skia.compose.PlotPanel

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

            PlayPauseButton(state.isGraphRunning, onClick = {
                if(state.isGraphRunning) {
                    logic.pauseGraph()
                } else {
                    logic.resumeGraph()
                }
            })
            VoltageGraph(state.figure)
        }
    }
}

@Composable
fun VoltageText(voltage: Double) {
    Text("Voltage: ${voltage}V")
}

@Composable
fun VoltageGraph(figure: Plot) {
    PlotPanel(figure = figure, modifier = Modifier.fillMaxSize()) {
        println("Message: $it")
    }
}

@Composable
fun PlayPauseButton(isPlaying: Boolean, onClick: () -> Unit) {
    // the buttons show what will happen when pressed, which is the opposite of current
    val icon = if(isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow

    IconButton(onClick = onClick) {
        Icon(icon, contentDescription = if(isPlaying) "Pause" else "Play")
    }
}
