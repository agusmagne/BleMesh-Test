package com.example.test_mesh

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import no.nordicsemi.android.log.LogSession
import no.nordicsemi.android.log.Logger
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.util.*

object Utils {

    val LOG_KEY = "LOG_KEY"
    val LOG_NAME = "LOG_NAME"

    val PROVISIONING_SUCCESS: Int = 2112
    val CONNECT_TO_NETWORK: Int = 2113
    val EXTRA_DEVICE: String = "EXTRA_DEVICE"
    val EXTRA_DATA_PROVISIONING_SERVICE: String = "EXTRA_DATA_PROVISIONING_SERVICE"

    fun toast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    fun isLocationEnabled(context: Context): Boolean {
        var locationMode = Settings.Secure.LOCATION_MODE_OFF
        try {
            locationMode =
                Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: Exception) {
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF
    }

    fun isBleEnabled(): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        return adapter != null && adapter.isEnabled
    }

    fun isLocationPermissionsGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getServiceData(scanResult: ScanResult, serviceUuid: UUID): ByteArray? {
        val scanRecord = scanResult.scanRecord
        if (scanRecord != null) {
            return scanRecord.getServiceData(ParcelUuid(serviceUuid))
        }
        return null
    }
}