package com.ank.rxr.bluetooth

import com.ank.core.BluetoothDevice
import com.ank.core.IBluetoothDeviceManager

class BluetoothDeviceManager: IBluetoothDeviceManager{
    override fun saveCurrentDevise(device: BluetoothDevice) {

    }

    override fun startDiscovery(onNext: BluetoothDevice) {

    }

    override fun stopDiscovery() {

    }

    override fun getCurrentDevice(): BluetoothDevice {
        return BluetoothDevice("s")
    }
}