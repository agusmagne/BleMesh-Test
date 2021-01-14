package com.example.test_mesh.di.main

import android.app.Application
import com.example.test_mesh.model.repositories.ScannerRepository
import dagger.Module
import dagger.Provides
import no.nordicsemi.android.mesh.MeshManagerApi

@Module
class MainModule {

    @Provides
    fun provideScannerRepository(
        application: Application,
        meshManagerApi: MeshManagerApi
    ): ScannerRepository =
        ScannerRepository(application, meshManagerApi)

}