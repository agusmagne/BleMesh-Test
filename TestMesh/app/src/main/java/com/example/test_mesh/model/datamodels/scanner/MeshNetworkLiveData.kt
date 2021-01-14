package com.example.test_mesh.model.datamodels.scanner

import androidx.lifecycle.LiveData
import no.nordicsemi.android.mesh.ApplicationKey
import no.nordicsemi.android.mesh.MeshNetwork

class MeshNetworkLiveData : LiveData<MeshNetworkLiveData>() {

    private var meshNetwork: MeshNetwork? = null
    lateinit var applicationKey: ApplicationKey
    lateinit var nodeName: String

    fun loadNetworkInformation(meshNetwork: MeshNetwork) {
        this.meshNetwork = meshNetwork
        postValue(this)
    }

    fun getNetwork() = meshNetwork

    fun setName(name: String) {
        if (name.isNotBlank()) {
            nodeName = name
            postValue(this)
        }
    }
}