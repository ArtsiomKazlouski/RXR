package com.ank.rxr

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.ank.core.BtDevice
import com.ank.rxr.bluetooth.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private var communicator: BtCommuncationHolder = BtCommuncationHolder()
    private var arduinoTimeConverter: SynchroTimeSeries = SynchroTimeSeries()

    private var startAt: Date? = null
    private var endAt: Date? = null
    private var device: BtDevice? = null

    companion object {
        const val DATE_FORMAT = "%02d:%02d:%03d"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        choose_bt.setOnClickListener {
            val intent = Intent(this, BluetoothSelectActivity::class.java)
            startActivity(intent)
        }

        relative_layout.setOnClickListener {
            copyTimeToBuffer()
        }

        device = RxrApplication.btManager.getCurrentDevice()
        val d = device

        refreshUiState()

        if (d != null) {
            communicator.run(d.address)
        }

        GlobalScope.launch {
            // launch a new coroutine in background and continue
            while (isActive) {
                delay(12L)
                if (startAt == null) {
                    continue
                }

                val end = if (endAt == null) Date() else endAt
                var millis = end!!.time - startAt!!.time
                var seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                seconds %= 60
                millis %= 1000

                runOnUiThread {
                    //showTime.text = String.format(DATE_FORMAT, minutes, seconds, millis)
                    if (millis <= 0 && seconds == 0 && minutes == 0 ){
                        showTime.text = "GO"
                    } else if (millis < 0 || seconds < 0 || minutes < 0) {
                        showTime.text = String.format("%01d", seconds * -1 )
                    } else {
                        showTime.text = String.format(DATE_FORMAT, minutes, seconds, millis)
                    }
                }
            }
        }

        var requestSynAt = Date()

        communicator.onDisconnect { ->
            arduinoTimeConverter.reset()
        }
        communicator.subscribe { message ->
            val key = "ack:"
            if (message.startsWith(key)) {

                val tuple = TimeSynhroArduino(requestSynAt, message.substring(key.length))
                arduinoTimeConverter.addMeasurement(tuple)
            }
        }

        GlobalScope.launch {
            while (isActive) {

                runOnUiThread {
                    refreshUiState()
                }

                if (!communicator.isActive()) {
                    delay(50L)
                    continue
                }

                val delay = if (arduinoTimeConverter.measurements.count() < 10) 200L else 5000L
                delay(delay)
                requestSynAt = Date()
                communicator.write("^syn;")
            }
        }

        startTimer.setOnClickListener { v ->
            val b = v as TextView
            if (b.text == "stop") {
                endAt = Date()
                b.text = "start"
                startTimer.background = resources.getDrawable(android.R.color.holo_green_light)
                iv_copy.visibility = View.VISIBLE
            } else {

                endAt = null
                if (communicator != null && arduinoTimeConverter.converterReady()) {
                    val c = Calendar.getInstance()
                    c.time = Date()
                    c.add(Calendar.SECOND, 4)
                    startAt = c.time
                    communicator.write("^start\$${arduinoTimeConverter.toArduinoTime(c.time)}")
                } else {
                    endAt = null
                    startAt = Date()
                }
                b.text = "stop"
                startTimer.background = resources.getDrawable(R.color.raspberry)
                iv_copy.visibility = View.GONE
            }
        }

    }

    private fun refreshUiState() {
        val d = device
        if (d == null) {
            choose_bt.text = "Choose traffic light device"
            choose_bt.setBackgroundColor(Color.parseColor("#FF33B5E5"))
            return
        }

        if (arduinoTimeConverter.measurements.count() < 10 || !communicator.isActive()) {
            choose_bt.text = "Connecting to ${d.name}"
            choose_bt.setBackgroundColor(Color.YELLOW)
            return
        }

        choose_bt.text = "Connected to ${device!!.name}"
        choose_bt.setBackgroundColor(Color.GREEN)
    }

    private fun copyTimeToBuffer() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Time", showTime.text)
        clipboard.primaryClip = clip
        Toast.makeText(this, "Time was copied", Toast.LENGTH_SHORT).show()
    }
}