package io.github.landrynorris.logic

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

fun SerialPort.linesFlow() = flow {
    val buffer = StringBuilder()

    dataFlow().collect { byteArray ->
        withContext(Dispatchers.Default) {
            buffer.append(byteArray.decodeToString())
            var index: Int
            while (buffer.indexOf("\n").also { index = it } != -1) {
                val line = buffer.substring(0, index).trim()
                if (line.isNotEmpty()) emit(line)
                buffer.delete(0, index + 1)
            }
        }
    }
}

fun SerialPort.dataFlow() = callbackFlow {
    addDataListener(object: SerialPortDataListener {
        override fun serialEvent(event: SerialPortEvent?) {
            event ?: return //skip if event is null

            trySendBlocking(event.receivedData)
        }

        override fun getListeningEvents(): Int {
            return SerialPort.LISTENING_EVENT_DATA_AVAILABLE
        }
    })
}
