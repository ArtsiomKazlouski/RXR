package com.ank.rxr.bluetooth

import java.net.CacheRequest
import java.util.*
import kotlin.collections.ArrayList

@UseExperimental(kotlin.ExperimentalUnsignedTypes::class)
class SynchroTimeSeries() {
    var measurements: ArrayList<TimeSynhroArduino> = arrayListOf()
    private var converter: ArduinoTimeConverter? = null

    fun addMeasurement(measurement: TimeSynhroArduino){
        if (measurement.getInFlight()<100){
            measurements.add(measurement)
            refreshConverter()
        }
    }

    fun refreshConverter(){
        if (measurements.count()<2){
            return
        }

        var firstMeasure = measurements.first()
        var lastMeasure = measurements.last()

        converter = ArduinoTimeConverter(firstMeasure, lastMeasure)
    }

    fun converterReady():Boolean{
        return converter!=null
    }

    fun toArduinoTime(date:Date):ULong{
        return converter!!.toArduinoTime(date)
    }
}

@UseExperimental(kotlin.ExperimentalUnsignedTypes::class)
class TimeSynhroArduino(val requestDate: Date, millis: String) {
    val response:Date = Date()
    val arduinoMillis:ULong
    init {
        arduinoMillis = millis.toULong()
    }

    fun getInFlight():Long{
        return response.time - requestDate.time
    }

    fun getEstimatedDeviceMillis():Long{
        return requestDate.time //+ (getInFlight().toFloat() * 0.5F).toLong()
    }
}

@UseExperimental(kotlin.ExperimentalUnsignedTypes::class)
class ArduinoTimeConverter(private val mf: TimeSynhroArduino,private val ml:TimeSynhroArduino){
    fun toArduinoTime(date:Date):ULong{
        var deviceDiff = ml.getEstimatedDeviceMillis() - mf.getEstimatedDeviceMillis()
        var arduinoDiff = ml.arduinoMillis - mf.arduinoMillis

        var diff = date.time - mf.getEstimatedDeviceMillis()

        var incDeviceDiff =  diff.toFloat() *  (arduinoDiff.toFloat() / deviceDiff.toFloat())

        return incDeviceDiff.toULong() + mf.arduinoMillis
    }
}