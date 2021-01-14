package com.example.test_mesh.viewmodel.scanner

import com.example.test_mesh.ble.BleMeshManager
import com.example.test_mesh.model.repositories.NrfMeshRepository
import com.example.test_mesh.model.repositories.ScannerRepository
import com.example.test_mesh.viewmodel.BaseViewModel
import javax.inject.Inject

class ScannerViewModel @Inject constructor(
    val scannerRepository: ScannerRepository,
    override val nrfMeshRepository: NrfMeshRepository
) : BaseViewModel(nrfMeshRepository) {

}