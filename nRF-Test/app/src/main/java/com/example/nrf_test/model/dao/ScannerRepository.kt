package com.example.nrf_test.model.dao

import android.util.Log
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult

class ScannerRepository {

    companion object {
        private const val TAG = "ScannerRepository"
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d(TAG, "onScanResult: ${result.advertisingSid}")
        }

        override fun onScanFailed(errorCode: Int) {
            Log.d(TAG, "onScanFailed: Scan FAILED code $errorCode")
        }
    }
}