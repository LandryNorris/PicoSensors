/*
 * Copyright 2025 Landry Norris
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.landrynorris.logic.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
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

@OptIn(ExperimentalMaterial3Api::class)
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
