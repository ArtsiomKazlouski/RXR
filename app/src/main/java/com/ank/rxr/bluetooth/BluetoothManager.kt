package com.ank.rxr.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.ank.core.BtDevice
import com.ank.core.IBluetoothDeviceManager
import com.ank.rxr.RxrApplication


class BtException(message: String): Exception(message)

class BluetoothDeviceManager: IBluetoothDeviceManager{

    val BLE_PREF_NAME = "Ble"
    val BLE_DEVICE_ADDRESS = "Address"
    val BLE_DEVICE_NAME = "NAME"

    var btAdapter: BluetoothAdapter

    init {
        btAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    override fun saveCurrentDevise(device: BtDevice) {

        var blePref = RxrApplication.getAppContext().getSharedPreferences(BLE_PREF_NAME, Context.MODE_PRIVATE)

        var editor = blePref.edit()

        editor.putString(BLE_DEVICE_ADDRESS, device.address)
        editor.putString(BLE_DEVICE_NAME, device.name)

        editor.apply()
    }

    override fun startDiscovery(onNext: (btDev: BtDevice) -> Unit) {
        if (!isBtEnabled()){
            throw BtException("Enable Bt")
        }

        for (btDevice in btAdapter.bondedDevices){
            onNext(BtDevice(btDevice.address, btDevice.name))
        }

        btAdapter.startDiscovery()

        val profileListener = object : BluetoothProfile.ServiceListener {

            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                if (profile == BluetoothProfile.STATE_CONNECTED) {
                    val device = proxy as BluetoothDevice
                    onNext(BtDevice(device.address, device.name))
                }
            }

            override fun onServiceDisconnected(profile: Int) {

            }
        }

        btAdapter?.getProfileProxy(RxrApplication.getAppContext(), profileListener, BluetoothProfile.STATE_CONNECTED)
    }

    override fun stopDiscovery() {
        btAdapter.cancelDiscovery()
    }

    override fun getCurrentDevice(): BtDevice? {

        var blePref = RxrApplication.getAppContext().getSharedPreferences(BLE_PREF_NAME, Context.MODE_PRIVATE)

        var deviceAddress = blePref.getString(BLE_DEVICE_ADDRESS, null)
        var deviceName = blePref.getString(BLE_DEVICE_NAME, null)

        if (deviceName == null || deviceAddress == null){
            return null
        }

        return BtDevice(deviceAddress, deviceName)
    }

    private fun isBtEnabled(): Boolean {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            return false
        } else {
            return btAdapter.isEnabled;
        }
    }
}