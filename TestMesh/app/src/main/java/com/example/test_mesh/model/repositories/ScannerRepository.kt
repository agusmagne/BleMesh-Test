package com.example.test_mesh.model.repositories

import android.app.Application
import android.os.ParcelUuid
import android.util.Log
import com.example.test_mesh.Utils
import com.example.test_mesh.ble.BleMeshManager
import com.example.test_mesh.model.datamodels.scanner.ScannerLiveData
import com.example.test_mesh.model.datamodels.scanner.ScannerStateLiveData
import no.nordicsemi.android.log.LogContract
import no.nordicsemi.android.log.Logger
import no.nordicsemi.android.mesh.MeshManagerApi
import no.nordicsemi.android.support.v18.scanner.*
import java.util.*
import javax.inject.Inject

class ScannerRepository @Inject constructor(
    private val application: Application,
    private val meshManagerApi: MeshManagerApi
) {

    val TAG = ScannerRepository::class.java.simpleName
    private val logSession = Logger.newSession(application, "key", "ScannerRepository")

    private var filterUuid: UUID? = null
    private lateinit var networkId: String
    private val scannerLiveData = ScannerLiveData()
    private val scannerStateLiveData =
        ScannerStateLiveData(Utils.isBleEnabled(), Utils.isLocationEnabled(application))

    fun startScan(filterUuid: UUID? = null) {
        scannerLiveData.clear()
        this.filterUuid = filterUuid

        if (scannerStateLiveData.isScanning()) return

        val asd = meshManagerApi.meshNetwork
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "network - $asd")
        asd?.let {
            it.nodes?.let { nodes ->
                if (nodes.isNotEmpty()) {
                    val b1 = asd.deleteNode(nodes.first())
                    Logger.log(logSession, LogContract.Log.Level.DEBUG, " -- $b1")
                }
            }
        }

        Logger.log(logSession, LogContract.Log.Level.DEBUG, "passed")
        if (filterUuid == BleMeshManager.MESH_PROXY_UUID) {
            val network = meshManagerApi.meshNetwork
            if (network != null) {
                networkId = meshManagerApi.generateNetworkId(network.netKeys[0].key)
            }
        }

        scannerStateLiveData.setScanningStarted(true)
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(0)
            .setUseHardwareFilteringIfSupported(false)
            .build()

        val filters = mutableListOf<ScanFilter>()
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(filterUuid)).build())

        val scanner = BluetoothLeScannerCompat.getScanner()
        scanner.startScan(filters, settings, scanCallbacks)
    }

    fun stopScan() {
        val scanner = BluetoothLeScannerCompat.getScanner()
        scanner.stopScan(scanCallbacks)
        scannerStateLiveData.setScanningStarted(false)
    }

    fun getScannerState(): ScannerStateLiveData = scannerStateLiveData

    fun getScanner(): ScannerLiveData = scannerLiveData

    private val scanCallbacks = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            try {
                if (filterUuid == BleMeshManager.MESH_PROVISIONING_UUID) {
//                    if (Utils.isLocationRequired(mContext) &&
//                        !Utils.isLocationEnabled(mContext)
//                    ) Utils.markLocationNotRequired(mContext)
                    updateScannerLiveData(result)
                }
            } catch (e: Exception) {
                Log.d(TAG, "onScanResult: ${e.message}")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            scannerStateLiveData.setScanningStarted(false)
        }
    }

    private fun updateScannerLiveData(result: ScanResult) {
        val scanRecord = result.scanRecord
        if (scanRecord != null) {
            if (scanRecord.bytes != null) {
                val beaconData = meshManagerApi.getMeshBeaconData(scanRecord.bytes!!)
                if (beaconData != null) {
                    scannerLiveData.deviceDiscovered(
                        result,
                        meshManagerApi.getMeshBeacon(beaconData)
                    )
                } else {
                    scannerLiveData.deviceDiscovered(result)
                }
                scannerStateLiveData.setDeviceFound()
            }
        }
    }

}