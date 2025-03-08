package io.github.landrynorris.logic

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

const val TARGET_VENDOR_ID = 0x2e8a
const val TARGET_PRODUCT_ID = 0x0009

const val POLLING_INTERVAL_MS = 1000L

fun List<SerialPort>.hasSameDevices(other: List<SerialPort>): Boolean {
    if(size != other.size) return false

    return zip(other).all { it.first.serialNumber == it.second.serialNumber }
}

object UsbDeviceDetector {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val mutableDevices = MutableSharedFlow<List<SerialPort>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private var lastList: List<SerialPort> = emptyList()
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

        val picoDevices = serialPorts
            .filter { it.vendorID == TARGET_VENDOR_ID && it.productID == TARGET_PRODUCT_ID }

        if (!picoDevices.hasSameDevices(lastList)) {
            mutableDevices.emit(picoDevices)
            lastList = picoDevices
        }
        delay(POLLING_INTERVAL_MS)
    }
}
