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
package io.github.landrynorris.logic

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

const val TARGET_VENDOR_ID = 0x2e8a
const val TARGET_PRODUCT_ID = 0x0009

const val POLLING_INTERVAL_MS = 1000L

fun List<SerialPort>.hasSameDevices(other: List<PicoSerialDevice>): Boolean {
    if(size != other.size) return false

    return zip(other).all { it.first.serialNumber == it.second.serialNumber }
}

object UsbDeviceDetector {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val mutableDevices = MutableSharedFlow<List<PicoSerialDevice>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private var lastList: List<PicoSerialDevice> = emptyList()
    val devices = mutableDevices.asSharedFlow()

    init {
        coroutineScope.launch {
            try {
                while(isActive) {
                    pollUsbDevices()
                }
            } catch(e: Exception) {
                println("Exception occurred while polling USB devices!")
            }
        }
    }

    private suspend fun pollUsbDevices() {
        val serialPorts = SerialPort.getCommPorts()

        val picoDevicePorts = serialPorts
            .filter { it.vendorID == TARGET_VENDOR_ID && it.productID == TARGET_PRODUCT_ID }

        if (!picoDevicePorts.hasSameDevices(lastList)) {
            // We need to ensure we don't lose the reference to our old picos
            val remaining = lastList.filter {
                picoDevicePorts.any { port -> port.serialNumber == it.serialNumber }
            }

            val new = picoDevicePorts.filter { port ->
                lastList.none { it.serialNumber == port.serialNumber }
            }.map { it.toPicoSerialDevice() }

            lastList = remaining + new

            mutableDevices.tryEmit(lastList)
        }
        delay(POLLING_INTERVAL_MS)
    }
}

private fun SerialPort.toPicoSerialDevice(): PicoSerialDevice {
    return PicoSerialDevice(this)
}
