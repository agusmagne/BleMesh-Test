package com.example.test_mesh.di.main

import androidx.lifecycle.ViewModel
import com.example.test_mesh.di.viewmodels.ViewModelKey
import com.example.test_mesh.viewmodel.scanner.ProvisioningViewModel
import com.example.test_mesh.viewmodel.scanner.ScannerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ScannerViewModel::class)
    abstract fun bindScannerViewModel(viewModel: ScannerViewModel): ViewModel


}