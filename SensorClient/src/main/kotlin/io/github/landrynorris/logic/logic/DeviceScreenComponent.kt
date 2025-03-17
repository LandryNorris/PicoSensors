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
package io.github.landrynorris.logic.logic

import com.arkivanov.decompose.ComponentContext
import io.github.landrynorris.logic.sensors.PicoSensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

interface DeviceScreenLogic {
    fun beginPollingData()
    fun stopPollingData()

    fun backPessed()
}

// TODO(Landry): Convert to a navigation library, such as Decompose
abstract class DeviceScreenComponent(
    val sensor: PicoSensor,
    val onBack: () -> Unit,
    context: ComponentContext
): ComponentContext by context, DeviceScreenLogic {
    val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val serialNumber get() = sensor.serialDevice.serialNumber

    init {
        sensor.serialDevice.addCloseListener {
            CoroutineScope(Dispatchers.Main).launch {
                onBack()
            }
        }
    }

    override fun backPessed() {
        onBack()
    }
}
