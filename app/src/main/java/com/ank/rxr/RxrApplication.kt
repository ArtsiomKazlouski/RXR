package com.ank.rxr

import android.app.Application
import android.content.Context
import com.ank.core.IBluetoothDeviceManager
import com.ank.rxr.bluetooth.BluetoothDeviceManager


class RxrApplication : Application() {

    companion object {

        private lateinit var context: Context

        fun getAppContext() : Context{
            return context;
        }
    }

    // Overriding this method is totally optional!
    override fun onCreate() {
        super.onCreate()
        // Required initialization logic here!
        var bManager:IBluetoothDeviceManager = BluetoothDeviceManager()

        context = applicationContext
    }
}