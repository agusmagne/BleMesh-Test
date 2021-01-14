package com.example.test_mesh.di.ble

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.example.test_mesh.ble.BleMeshManager
import com.example.test_mesh.model.repositories.NrfMeshRepository
import dagger.Module
import dagger.Provides
import dagger.Reusable
import no.nordicsemi.android.mesh.MeshManagerApi

@Module
class BleMeshManagerModule {

    @Provides
    @Reusable
    fun provideBleMeshManager(application: Application, meshManagerApi: MeshManagerApi): BleMeshManager =
        BleMeshManager(application, meshManagerApi)

    @Provides
    @Reusable
    fun provdeMeshManagerApi(application: Application): MeshManagerApi = MeshManagerApi(application)

    @Provides
    @Reusable
    fun provideNrfMeshRepository(meshManagerApi: MeshManagerApi, bleMeshManager: BleMeshManager) =
        NrfMeshRepository(meshManagerApi, bleMeshManager)
}