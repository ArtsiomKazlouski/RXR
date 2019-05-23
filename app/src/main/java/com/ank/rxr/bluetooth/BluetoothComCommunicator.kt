package com.ank.rxr.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.StringBuilder
import java.util.*


abstract class ComMessage{
    abstract fun serialize():String
    abstract fun deserialize(message: String)
}

class BluetoothComCommunicatorFactory {

    val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun createCommunicator(address: String):BluetoothComCommunicator{
        var btAdapter = BluetoothAdapter.getDefaultAdapter()
        val device = btAdapter.getRemoteDevice(address)

        var socket = device.createRfcommSocketToServiceRecord(MY_UUID)
        socket.connect()
        return BluetoothComCommunicator(socket)
    }
}

class BluetoothComCommunicator(val socket: BluetoothSocket) {

    init {
        socket.isConnected
    }
    fun write(message: String){
        socket.outputStream.write(message.toByteArray())
    }

    fun subscribe(onMessageReceived: (message: String) -> Unit){
        var btJob = GlobalScope.launch {
            val mmInStream = socket.inputStream

            val buffer = ByteArray(256)  // buffer store for the stream
            var bytes: Int // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs

            var sb = StringBuilder()
            while (isActive) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream!!.read(buffer)        // Get number of bytes and message in "buffer"
                    val strIncom = String(buffer, 0, bytes)
                    sb.append(strIncom)
                    val endOfLineIndex = sb.indexOf("\r\n")
                    if (endOfLineIndex > 0){
                        val message = sb.substring(0, endOfLineIndex)
                        onMessageReceived(message)
                        sb.delete(0, sb.length)
                    }

                } catch (e: IOException) {
                    break
                }
            }
        }
    }
}