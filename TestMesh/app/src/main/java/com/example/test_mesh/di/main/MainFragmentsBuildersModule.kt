package com.example.test_mesh.di.main

import com.example.test_mesh.di.main.scanner.ScannerViewModelsModule
import com.example.test_mesh.view.scanner.ScannerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentsBuildersModule {

    @ContributesAndroidInjector(modules = [
        ScannerViewModelsModule::class
    ])
    abstract fun contributeScannerFragment(): ScannerFragment
}