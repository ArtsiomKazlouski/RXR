package com.ank.core

interface IBluetoothDeviceManager{
    fun getCurrentDevice(): BtDevice?
    fun saveCurrentDevise(device:BtDevice)
    fun startDiscovery(onNext: (btDev:BtDevice) -> Unit)
    fun stopDiscovery()
}

data class BtDevice(
    val address: String,
    val name: String
)