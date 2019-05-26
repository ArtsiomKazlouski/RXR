package com.ank.rxr

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
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
import android.provider.CalendarContract
import android.support.annotation.ColorInt
import com.ank.core.BtDevice
import com.ank.rxr.bluetooth.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var communicator: BtCommuncationHolder = BtCommuncationHolder()

    val DATE_FORMAT = "%02d:%02d:%03d"

    lateinit var chuseBtButton:Button;
    lateinit var startTimer:Button;
    lateinit var timerView:TextView;
    var arduinoTimeConverter: SynchroTimeSeries = SynchroTimeSeries()

    var startAt:Date? = null
    var endAt:Date? = null
    var device: BtDevice? = null

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



        device = RxrApplication.btManager.getCurrentDevice()
        var d = device

        refreshUiState()

        if (d!=null){
            communicator.run(d!!.address)
        }

        var job = GlobalScope.launch {
            // launch a new coroutine in background and continue
            while (isActive){
                delay(12L)
                if (startAt == null){
                    continue
                }

                var end = if(endAt == null) Date() else endAt
                var millis =end!!.time - startAt!!.time
                var seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                seconds %= 60
                millis %=1000

                runOnUiThread {

                    if (millis < -1000){
                        timerView.text = String.format("%01d", seconds*-1)
                    }else{
                        timerView.text = String.format(DATE_FORMAT, minutes, seconds, millis)
                    }

                }
            }
        }

        var requestSynAt = Date()

        communicator.onDisconnect { ->
            arduinoTimeConverter.reset()
        }
        communicator!!.subscribe { message ->
            val key = "ack:"
            if(message.startsWith(key)){

                var tuple = TimeSynhroArduino(requestSynAt, message.substring(key.length))
                arduinoTimeConverter.addMeasurement(tuple)
            }
        }

        var synchronizationJob = GlobalScope.launch {
            while (isActive){

                var d = device

                runOnUiThread {
                    refreshUiState()
                }

                if (communicator.isActive() == false){
                    delay(50L)
                    continue
                }

                val delay = if(arduinoTimeConverter.measurements.count()<10) 200L else 5000L
                delay(delay)
                requestSynAt = Date()
                communicator!!.write("^syn;")
            }
        }

        startTimer.setOnClickListener { v ->
            val b = v as Button
            if (b.text == "stop") {
                endAt = Date()
                b.text = "start"
            } else {

                endAt = null
                if (communicator!=null && arduinoTimeConverter.converterReady()){
                    val c = Calendar.getInstance()
                    c.time = Date()
                    c.add(Calendar.SECOND, 4)
                    startAt = c.time
                    communicator!!.write("^start\$${arduinoTimeConverter.toArduinoTime(c.time)};");
                }else{
                    endAt = null
                    startAt = Date()
                }
                b.text = "stop"
            }
        }

    }

    fun refreshUiState(){
        var d = device
        if (d == null){
            chuseBtButton.text = "Chose traffic light device"
            chuseBtButton.setBackgroundColor(Color.parseColor("#FF33B5E5"))
            return
        }

        if(arduinoTimeConverter.measurements.count()<10 || !communicator.isActive()){
            chuseBtButton.text = "Connecting to ${d.name}"
            chuseBtButton.setBackgroundColor(Color.YELLOW)
            return
        }

        chuseBtButton.text = "Connected to ${device!!.name}"
        chuseBtButton.setBackgroundColor(Color.GREEN)
    }
}