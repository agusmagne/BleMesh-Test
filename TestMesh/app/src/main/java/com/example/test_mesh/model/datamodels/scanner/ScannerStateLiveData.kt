package com.example.test_mesh.model.datamodels.scanner

import androidx.lifecycle.LiveData

class ScannerStateLiveData(
    private var bluetoothEnabled: Boolean,
    private var locationEnabled: Boolean,
    private var scanningStarted: Boolean = false,
    private var deviceFound: Boolean = false
) : LiveData<ScannerStateLiveData>() {

    fun startScanning() = postValue(this)

    fun setScanningStarted(started: Boolean) {
        scanningStarted = started

        // SCANNING STOPPED
        if (!started) {
            deviceFound = !started
//            postValue(this)
        }
    }

    fun setBluetoothEnabled(enabled: Boolean) {
        bluetoothEnabled = enabled
        postValue(this)
    }

    fun setLocationEnabled(enabled: Boolean) {
        locationEnabled = enabled
        postValue(this)
    }

    fun setDeviceFound() {
        if (!deviceFound) {
            deviceFound = true
            postValue(this)
        }
    }

    fun isEmpty(): Boolean = !deviceFound

    fun isScanning(): Boolean = scanningStarted

    fun isBluetoothEnabled(): Boolean = bluetoothEnabled

    fun isLocationEnabled(): Boolean = locationEnabled
}