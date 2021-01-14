package com.example.test_mesh.viewmodel.scanner

import androidx.lifecycle.LiveData
import com.example.test_mesh.model.repositories.NrfMeshRepository
import com.example.test_mesh.viewmodel.BaseViewModel
import no.nordicsemi.android.mesh.provisionerstates.UnprovisionedMeshNode
import javax.inject.Inject

class ProvisioningViewModel @Inject constructor(
    override val nrfMeshRepository: NrfMeshRepository
) : BaseViewModel(nrfMeshRepository) {
}