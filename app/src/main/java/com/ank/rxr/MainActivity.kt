package com.ank.rxr

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.os.Handler.Callback;
import android.os.Message
import android.support.v4.os.HandlerCompat.postDelayed
import android.R.string.cancel






class MainActivity : AppCompatActivity() {

    lateinit var chuseBtButton:Button;
    lateinit var startTimer:Button;
    lateinit var stopTimer:Button;
    lateinit var timerView:TextView;

    lateinit var startAt:Date
    var endAt:Date? = null
    var timer = Timer()

    val h = Handler(object : Callback {

        override fun handleMessage(msg: Message): Boolean {
            var end = if(endAt == null) Date() else endAt
            var millis =end!!.time - startAt.time
            var seconds = (millis / 1000).toInt()
            val minutes = seconds / 60
            seconds %= 60
            millis %=1000

            timerView.text = String.format("%02d:%02d:%03d", minutes, seconds, millis)
            return false
        }
    })

    //runs without timer be reposting self
    var h2 = Handler()
    var run: Runnable = object : Runnable {

        override fun run() {
            h2.postDelayed(this, 100)
        }
    }

    //tells handler to send a message
    internal inner class firstTask : TimerTask() {

        override fun run() {
            h.sendEmptyMessage(0)
        }
    };

    //tells activity to run on ui thread
    internal inner class secondTask : TimerTask() {

        override fun run() {
            runOnUiThread(Runnable {

            })
        }
    };

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        chuseBtButton = findViewById<Button>(R.id.chuse_bt)
        chuseBtButton.setOnClickListener { v ->
            val intent = Intent(this, BluetoothSelectActivity::class.java)
            startActivity(intent)
        }
        timerView = findViewById<TextView>(R.id.showTime)
        startTimer = findViewById<Button>(R.id.startTimer)
        stopTimer = findViewById<Button>(R.id.stopTimer)

        startTimer.setOnClickListener { v ->
            val b = v as Button
            if (b.text == "stop") {
                timer.cancel()
                timer.purge()
                //h2.removeCallbacks(run)
                b.text = "start"
            } else {
                startAt = Date()
                timer = Timer()
                timer.schedule(firstTask(), 0, 75)
                //h2.postDelayed(run, 0)
                b.text = "stop"
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

//internal class UpdateTimerTask(val textView:TextView) : TimerTask() {
//
//    lateinit var startAt:Date
//    var endAt:Date? = null
//
//    fun start(){
//        startAt = Date()
//        endAt = null
//    }
//
//    override fun run() {
//        var end = if(endAt == null) Date() else endAt
//
//        val diff = startAt.time - end!!.time
//        val seconds = diff / 1000
//        val minutes = seconds / 60
//        val hours = minutes / 60
//        val days = hours / 24
//
//        textView.text = "$minutes $seconds"
//    }
//
//    fun stop(){
//        endAt = Date()
//    }
//}


//class MainActivity : AppCompatActivity() {
//
//    val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//    lateinit var  btAdapter: BluetoothAdapter
//    lateinit var btSocket: BluetoothSocket
//
//    public var h: Handler? = null
//
//    public val RECIEVE_MESSAGE = 1        // Status  for Handler
//    private val sb = StringBuilder()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)
//
//        h = object : Handler() {
//            override fun handleMessage(msg: android.os.Message) {
//                when (msg.what) {
//                    RECIEVE_MESSAGE -> {
//                        val readBuf = msg.obj as ByteArray
//                        val strIncom = String(readBuf, 0, msg.arg1)
//                        sb.append(strIncom)
//                        val endOfLineIndex = sb.indexOf("\r\n")
//                        showText.setText("Data from Arduino: $sb")
//                        if (endOfLineIndex > 0) {
//                            val sbprint = sb.substring(0, endOfLineIndex)
//                            sb.delete(0, sb.length)
//                            showText.setText("Data from Arduino: $sbprint")
////                            if (flag % 4 === 3) {
////                                rlayout.setBackgroundColor(Color.rgb(255, 255, 255))
////                            } else if (flag % 4 === 1) {
////                                rlayout.setBackgroundColor(Color.rgb(255, 0, 0))
////                            } else if (flag % 4 === 2) {
////                                rlayout.setBackgroundColor(Color.rgb(0, 255, 0))
////                            } else if (flag % 4 === 0) {
////                                rlayout.setBackgroundColor(Color.rgb(0, 0, 255))
////                            }
////                            flag++
////                            btnLed1.setEnabled(true)
////                            btnLed2.setEnabled(true)
////                            btnLed3.setEnabled(true)
////                            btnpado.setEnabled(true)
//                        }
//
//                    }
//                }
//            }
//        }
//
//
//        btAdapter = BluetoothAdapter.getDefaultAdapter()
//        checkBTState()
//        val device = btAdapter.getRemoteDevice("00:21:13:05:B8:69")
//
//        var socket = createBluetoothSocket(device)
//
//        socket.connect()
//        val thread = ConnectedThread(socket, h as Handler)
//        thread.start()
//
//        thread.write("test")
////        for (bt in  btAdapter.bondedDevices){
////            var address = bt.address
////            var name = bt.name
////        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        //val device = btAdapter.ge(address)
//    }
//
//    private fun createBluetoothSocket(device: BluetoothDevice): BluetoothSocket {
//        return device.createRfcommSocketToServiceRecord(MY_UUID)
//    }
//
//    private fun checkBTState() {
//        // Check for Bluetooth support and then check to make sure it is turned on
//        // Emulator doesn't support Bluetooth and will return null
//        if (btAdapter == null) {
//            errorExit("Fatal Error", "Bluetooth not support")
//        } else {
//            if (!btAdapter.isEnabled) {
//                //Prompt user to turn on Bluetooth
//                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                startActivityForResult(enableBtIntent, 1)
//            }
//        }
//    }
//
//    private fun errorExit(title: String, message: String) {
//        Toast.makeText(baseContext, "$title - $message", Toast.LENGTH_LONG).show()
//        finish()
//    }
//
//    private class ConnectedThread(socket: BluetoothSocket,val h: Handler) : Thread() {
//        private val mmInStream: InputStream?
//        private val mmOutStream: OutputStream?
//
//        init {
//            var tmpIn: InputStream? = null
//            var tmpOut: OutputStream? = null
//
//            // Get the input and output streams, using temp objects because
//            // member streams are final
//            try {
//                tmpIn = socket.inputStream
//                tmpOut = socket.outputStream
//            } catch (e: IOException) {
//            }
//
//            mmInStream = tmpIn
//            mmOutStream = tmpOut
//        }
//
//        override fun run() {
//            val buffer = ByteArray(256)  // buffer store for the stream
//            var bytes: Int // bytes returned from read()
//
//            // Keep listening to the InputStream until an exception occurs
//            while (true) {
//                try {
//                    // Read from the InputStream
//                    bytes = mmInStream!!.read(buffer)        // Get number of bytes and message in "buffer"
//
//                    h.obtainMessage(1, bytes, -1, buffer).sendToTarget()     // Send to message queue Handler
//                } catch (e: IOException) {
//                    break
//                }
//
//            }
//        }
//
//        /* Call this from the main activity to send data to the remote device */
//        fun write(message: String) {
//            val msgBuffer = message.toByteArray()
//            try {
//                mmOutStream!!.write(msgBuffer)
//            } catch (e: IOException) {
//
//            }
//
//        }
//    }
//
//}


