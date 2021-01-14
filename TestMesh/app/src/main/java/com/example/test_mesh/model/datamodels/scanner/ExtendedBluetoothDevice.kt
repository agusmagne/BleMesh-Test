package com.example.test_mesh.model.datamodels.scanner

import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import no.nordicsemi.android.mesh.MeshBeacon
import no.nordicsemi.android.support.v18.scanner.ScanResult

@Parcelize
class ExtendedBluetoothDevice(
    val scanResult: ScanResult,
    val beacon: MeshBeacon?,
    var rssi: Int = scanResult.rssi,
    val device: BluetoothDevice = scanResult.device,
    var name: String = "Device Unknown"
) : Parcelable {

    fun matches(scanResult: ScanResult): Boolean {
        return device.address == scanResult.device.address
    }
}