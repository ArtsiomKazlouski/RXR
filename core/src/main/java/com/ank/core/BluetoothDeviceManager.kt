package com.ank.core

interface IBluetoothDeviceManager{
    fun getCurrentDevice(): BluetoothDevice
    fun saveCurrentDevise(device:BluetoothDevice)
    fun startDiscovery(onNext: (BluetoothDevice))
    fun stopDiscovery()
}

data class BluetoothDevice(
    val address: String
)