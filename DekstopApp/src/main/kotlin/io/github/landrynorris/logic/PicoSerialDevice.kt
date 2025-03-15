package io.github.landrynorris.logic

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import kotlinx.coroutines.flow.*

class PicoSerialDevice(serialPort: SerialPort) {
    private val dataFlow = MutableSharedFlow<ByteArray>(extraBufferCapacity = 128)

    val serialNumber = serialPort.serialNumber
    val systemPortPath = serialPort.systemPortPath

    // stores partial lines for the data flow
    private var buffer = ""
    val linesFlow: Flow<String> = dataFlow
        .map { chunk -> processChunk(chunk) } // Process data into complete lines
        .filter { it.isNotEmpty() } // Filter out empty emissions
        .flatMapConcat { lines -> lines.asFlow() } // Emit each line separately
        .drop(1)

    var isOpen = false; private set

    init {
        serialPort.openPort()
        isOpen = true

        serialPort.addDataListener(object: SerialPortDataListener {
            override fun getListeningEvents(): Int {
                return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED or SerialPort.LISTENING_EVENT_DATA_AVAILABLE
            }

            override fun serialEvent(event: SerialPortEvent?) {
                event ?: return

                if(event.eventType == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
                    serialPort.closePort()
                    isOpen = false
                } else if(event.eventType == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    val data = serialPort.availableData()
                    onDataAvailable(data)
                }
            }
        })
    }

    fun onDataAvailable(data: ByteArray) {
        dataFlow.tryEmit(data)
    }

    // Process incoming data and extract complete lines
    private fun processChunk(chunk: ByteArray): List<String> {
        val text = chunk.toString(Charsets.UTF_8)
        val split = (buffer + text).split("\n") // Split using newline
        buffer = if (text.endsWith("\n")) "" else split.last() // Store last fragment
        return split.dropLast(1) // Return complete lines
    }
}

private fun SerialPort.availableData(): ByteArray {
    return ByteArray(bytesAvailable()).also { readBytes(it, it.size) }
}
