package com.example.test_mesh.ble

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import com.example.test_mesh.Utils
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.log.LogContract
import no.nordicsemi.android.log.LogSession
import no.nordicsemi.android.log.Logger
import no.nordicsemi.android.mesh.MeshManagerApi
import java.util.*
import javax.inject.Inject

class BleMeshManager @Inject constructor(
    private val application: Application,
    private val meshManagerApi: MeshManagerApi
) : BleManager(application) {

    private val MTU_MAX_SIZE: Int = 517

    var logSession: LogSession? = null

    init {
        if (logSession == null) {
            logSession = Logger.newSession(application, "Ble Mesh Manager", "Ble Mesh Manager")
        }
    }

    companion object {
        val MESH_PROXY_UUID = UUID.fromString("00001828-0000-1000-8000-00805F9B34FB")
        val MESH_PROVISIONING_UUID = UUID.fromString("00001827-0000-1000-8000-00805F9B34FB")
    }

    private val MESH_PROVISIONING_DATA_IN = UUID.fromString("00002ADB-0000-1000-8000-00805F9B34FB")
    private val MESH_PROVISIONING_DATA_OUT = UUID.fromString("00002ADC-0000-1000-8000-00805F9B34FB")
    private val MESH_PROXY_DATA_IN = UUID.fromString("00002ADD-0000-1000-8000-00805F9B34FB")
    private val MESH_PROXY_DATA_OUT = UUID.fromString("00002ADE-0000-1000-8000-00805F9B34FB")

    private var isDeviceReady = false
    private var isProvisioningComplete = false

    private var meshProvisioningDataInCharacteristic: BluetoothGattCharacteristic? = null
    private var meshProvisioningDataOutCharacteristic: BluetoothGattCharacteristic? = null

    private var meshProxyDataInCharacteristic: BluetoothGattCharacteristic? = null
    private var meshProxyDataOutCharacteristic: BluetoothGattCharacteristic? = null

    private var gattCallback: BleManagerGattCallback? = null

    override fun getGattCallback(): BleManagerGattCallback {
        return if (gattCallback == null) {
            gattCallback = buildGattCallback()
            gattCallback as BleManagerGattCallback
        } else {
            gattCallback as BleManagerGattCallback
        }
    }

    private fun buildGattCallback() = object : BleManagerGattCallback() {
        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            Logger.log(logSession, LogContract.Log.Level.DEBUG, "isRequiredServiceSupported")
            val meshProxyService = gatt.getService(MESH_PROXY_UUID)
            if (meshProxyService != null) {
                isProvisioningComplete = true
                meshProxyDataInCharacteristic = meshProxyService.getCharacteristic(
                    MESH_PROXY_DATA_IN
                )
                meshProxyDataOutCharacteristic = meshProxyService.getCharacteristic(
                    MESH_PROXY_DATA_OUT
                )
                val bool =
                    meshProxyDataInCharacteristic != null && meshProxyDataOutCharacteristic != null &&
                            hasNotifyProperty(meshProxyDataOutCharacteristic) &&
                            hasWriteNoResponseProperty(meshProxyDataInCharacteristic)
                Logger.log(logSession, LogContract.Log.Level.DEBUG, "meshProxyService -> $bool")
                return bool
            }

            val meshProvisioningService = gatt.getService(MESH_PROVISIONING_UUID)
            if (meshProvisioningService != null) {
                isProvisioningComplete = false
                meshProvisioningDataInCharacteristic =
                    meshProvisioningService.getCharacteristic(
                        MESH_PROVISIONING_DATA_IN
                    )
                meshProvisioningDataOutCharacteristic =
                    meshProvisioningService.getCharacteristic(
                        MESH_PROVISIONING_DATA_OUT
                    )
                val bool =
                    meshProvisioningDataInCharacteristic != null && meshProvisioningDataOutCharacteristic != null &&
                            hasNotifyProperty(meshProvisioningDataOutCharacteristic) &&
                            hasWriteNoResponseProperty(meshProvisioningDataInCharacteristic)
                Logger.log(
                    logSession,
                    LogContract.Log.Level.DEBUG,
                    "meshProvisioningService -> $bool"
                )
                return bool
            }
            return false
        }

        override fun onDeviceReady() {
            Logger.log(logSession, LogContract.Log.Level.DEBUG, "onDeviceReady")
            isDeviceReady = true
            super.onDeviceReady()
        }

        override fun onDeviceDisconnected() {
            Logger.log(logSession, LogContract.Log.Level.DEBUG, "onDeviceReady")
            isDeviceReady = false
            isProvisioningComplete = false
        }

        override fun initialize() {
            Logger.log(logSession, LogContract.Log.Level.DEBUG, "initialize")
            requestMtu(MTU_MAX_SIZE).enqueue()

            val characteristic =
                if (isProvisioningComplete) meshProxyDataOutCharacteristic else meshProvisioningDataOutCharacteristic

            setNotificationCallback(characteristic).with { device, data ->
                onDataReceived(device, getMaximumPacketSize(), data.value)
            }
            enableNotifications(characteristic).enqueue()
        }

    }

    fun sendPdu(pdu: ByteArray) {
        if (!isDeviceReady) return
        val characteristic = if (isProvisioningComplete) {
            meshProxyDataInCharacteristic
        } else {
            meshProvisioningDataInCharacteristic
        }
        writeCharacteristic(characteristic, pdu)
            .split()
            .with { device, data -> onDataSent(device, getMaximumPacketSize(), data.value) }
            .enqueue()
    }

    fun getMaximumPacketSize(): Int {
        return super.getMtu() - 3
    }

    private fun onDataReceived(device: BluetoothDevice, mtu: Int, value: ByteArray?) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onDataReceived")
        value?.let { meshManagerApi.handleNotifications(mtu, it) }
    }

    private fun onDataSent(device: BluetoothDevice, mtu: Int, value: ByteArray?) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onDataSent")
        value?.let { meshManagerApi.handleWriteCallbacks(mtu, it) }
    }


    private fun hasWriteNoResponseProperty(characteristic: BluetoothGattCharacteristic?): Boolean {
        return (characteristic?.properties?.and(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0
    }

    private fun hasNotifyProperty(characteristic: BluetoothGattCharacteristic?): Boolean {
        return (characteristic?.properties?.and(BluetoothGattCharacteristic.PROPERTY_NOTIFY)) != 0
    }
}