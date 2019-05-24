package com.ank.rxr.bluetooth

import java.net.CacheRequest
import java.util.*

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

    fun getConverter():ArduinoTimeConverter{
        var inFlightMilis = getInFlight()
        var deviceMillis = requestDate.time + inFlightMilis/2
        return ArduinoTimeConverter(arduinoMillis, deviceMillis)
    }

}

@UseExperimental(kotlin.ExperimentalUnsignedTypes::class)
class ArduinoTimeConverter(private val arduinoTime: ULong,private val deviceTime:Long){
    fun toArduinoTime(date:Date):ULong{
        var diff = date.time - deviceTime
        return diff.toULong() + arduinoTime
    }
}