package com.example.test_mesh.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.test_mesh.model.datamodels.scanner.ExtendedBluetoothDevice
import com.example.test_mesh.model.repositories.NrfMeshRepository

abstract class BaseViewModel(open val nrfMeshRepository: NrfMeshRepository) : ViewModel() {

    fun navigateToScannerFragment() {

    }

    fun resetMeshNetwork() {
        nrfMeshRepository.resetMeshNetwork()
    }

    fun connect(context: Context, device: ExtendedBluetoothDevice, connectToNetwork: Boolean) {
        nrfMeshRepository.connect(context, device, connectToNetwork)
    }

    fun disconnect() {
        nrfMeshRepository.disconnect()
    }

}