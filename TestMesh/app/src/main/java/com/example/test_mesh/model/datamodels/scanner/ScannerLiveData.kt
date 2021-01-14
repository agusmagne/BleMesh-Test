package com.example.test_mesh.model.datamodels.scanner

import androidx.lifecycle.LiveData
import no.nordicsemi.android.mesh.MeshBeacon
import no.nordicsemi.android.support.v18.scanner.ScanResult

class ScannerLiveData : LiveData<ScannerLiveData>() {

    private val devices = mutableListOf<ExtendedBluetoothDevice>()
    private var updatedDeviceIndex: Int? = null

    fun deviceDiscovered(scanResult: ScanResult, beacon: MeshBeacon? = null) {
        val index = indexOf(scanResult)
        val device: ExtendedBluetoothDevice
        if (index == -1) {
            device = ExtendedBluetoothDevice(scanResult = scanResult, beacon = beacon)
            devices.add(device)
            updatedDeviceIndex = null
        } else {
            device = devices[index]
            updatedDeviceIndex = index
        }
        device.rssi = scanResult.rssi
        device.name = getDeviceName(scanResult)

        postValue(this)
    }

    fun clear() {
        devices.clear()
        updatedDeviceIndex = null
        postValue(this)
    }

    fun getDevices(): MutableList<ExtendedBluetoothDevice> = devices

    fun getUpdatedDeviceIndex(): Int? {
        val i = updatedDeviceIndex
        updatedDeviceIndex = null
        return i
    }

    private fun indexOf(scanResult: ScanResult): Int {
        for ((index, device) in devices.withIndex()) {
            if (device.matches(scanResult)) return index
        }
        return -1
    }

    private fun getDeviceName(scanResult: ScanResult): String {
        return if (scanResult.scanRecord != null) {
            scanResult.scanRecord?.deviceName!!
        } else {
            "Unkown"
        }
    }
}