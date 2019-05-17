package com.ank.core

interface IBluetoothDeviceManager{
    fun getCurrentDevice(): BtDevice?
    fun saveCurrentDevise(device:BtDevice)
    fun startListenForConnectedDevices(onNext: (btDev:BtDevice) -> Unit)
    fun stopListen()
}

data class BtDevice(
    val address: String,
    val name: String
)