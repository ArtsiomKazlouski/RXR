package com.ank.rxr.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList


class BtCommuncationHolder{
    private val socketFactory = BluetoothSocketFactory()
    private val subscriptions = ArrayList<(message: String) -> Unit>()
    private var communicator: BluetoothComCommunicator? = null
    val receiveMessageQueue: Queue<String> = LinkedList<String>()
    val sendMessageQueue: Queue<String> = LinkedList<String>()


    private var onDisconnected:(() -> Unit)? = null
    private var socket:BluetoothSocket? = null

    fun run(address: String){
        disconnect()
        GlobalScope.launch {
            while (isActive){
                try {
                    val s = socket
                    if (s == null){
                        val s = socketFactory.openSocketAndConnect(address)
                        socket = s
                        communicator = BluetoothComCommunicator(s){m ->
                            receiveMessageQueue.add(m)
                        }
                        continue
                    }
                    if (s!!.isConnected){
                        continue
                    }else{
                        socket = null
                    }
                }
                catch (e: IOException){
                    disconnect()
                }
            }
        }
    }

    init {
        var subscriptionJob = GlobalScope.launch {
            while (isActive){
                var m = receiveMessageQueue.poll()
                if (m == null){
                    continue
                }
                val s = subscriptions.count()
                for (s in subscriptions){
                    s.invoke(m)
                }
            }
        }

        var writeJob = GlobalScope.launch {
            while (isActive){

                while (true){
                    var m = sendMessageQueue.peek()
                    if (m == null){
                        continue
                    }
                    if (socket == null) {
                        delay(10L)
                        continue
                    }
                    try {
                        socket!!.outputStream.write(m.toByteArray())
                        sendMessageQueue.remove(m)
                    }
                    catch (e: IOException){
                        disconnect()
                    }
                }
            }
        }
    }

    fun write(message: String){
        sendMessageQueue.add(message)
    }

    fun subscribe(onMessageReceived: (message: String) -> Unit){
        subscriptions.add(onMessageReceived)
    }

    fun isActive():Boolean{
        var s = socket
        var c = communicator
        return s != null && s.isConnected && c != null
    }

    private fun disconnect(){
        val s = socket
        if (s!=null){
            s.close()
        }
        socket = null
        communicator = null
        var d = onDisconnected
        if (d != null){
            d.invoke()
        }
    }

    fun onDisconnect(onDisconnected: () -> Unit){
        this.onDisconnected = onDisconnected
    }
}


class BluetoothSocketFactory {
    val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    lateinit var communicator: BluetoothComCommunicator

    fun openSocketAndConnect(address: String):BluetoothSocket{
        var btAdapter = BluetoothAdapter.getDefaultAdapter()
        val device = btAdapter.getRemoteDevice(address)

        var socket = device.createRfcommSocketToServiceRecord(MY_UUID)
        socket.connect()

        return socket
    }
}

class BluetoothComCommunicator(val socket: BluetoothSocket, onMessageReceived: (message: String) -> Unit) {
    init {
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
                    val eol = "\r\n"
                    val endOfLineIndex = sb.indexOf("\r\n")
                    if (endOfLineIndex == 0 && sb.startsWith(eol)){
                        sb.delete(0, eol.length)
                    }
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

    fun write(message: String){
        socket.outputStream.write(message.toByteArray())
    }
}