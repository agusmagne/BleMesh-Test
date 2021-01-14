package com.example.test_mesh.ble

import android.bluetooth.BluetoothDevice

interface BleManagerCallbacks {

    fun onDeviceConnecting(device: BluetoothDevice)

    fun onDeviceConnected(device: BluetoothDevice)

    fun onDeviceDisconnecting(device: BluetoothDevice)

    fun onDeviceDisconnected(device: BluetoothDevice)

    fun onServicesDiscovered(device: BluetoothDevice, optionalServicesFound: Boolean)

    fun onDeviceReady(device: BluetoothDevice)

}