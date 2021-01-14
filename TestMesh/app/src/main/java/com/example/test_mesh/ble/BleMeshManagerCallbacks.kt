package com.example.test_mesh.ble

import android.bluetooth.BluetoothDevice

interface BleMeshManagerCallbacks : BleManagerCallbacks {

    fun onDataReceived(device: BluetoothDevice, mtu: Int, pdu: ByteArray?)

    fun onDataSent(device: BluetoothDevice, mtu: Int, pdu: ByteArray?)

}