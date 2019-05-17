package com.ank.rxr

import android.app.Application
import android.content.Context
import com.ank.core.IBluetoothDeviceManager
import com.ank.rxr.bluetooth.BluetoothDeviceManager


class RxrApplication : Application() {

    companion object {

        lateinit var context: Context
        lateinit var btManager: IBluetoothDeviceManager
    }

    // Overriding this method is totally optional!
    override fun onCreate() {
        super.onCreate()
        // Required initialization logic here!
        btManager = BluetoothDeviceManager()
        context = applicationContext
    }
}