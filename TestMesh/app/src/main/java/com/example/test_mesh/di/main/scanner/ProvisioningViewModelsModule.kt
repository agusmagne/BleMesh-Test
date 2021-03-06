package com.example.test_mesh.di.main.scanner

import androidx.lifecycle.ViewModel
import com.example.test_mesh.di.viewmodels.ViewModelKey
import com.example.test_mesh.viewmodel.scanner.ProvisioningViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ProvisioningViewModelsModule {


    @Binds
    @IntoMap
    @ViewModelKey(ProvisioningViewModel::class)
    abstract fun bindProvisioningViewModel(viewModel: ProvisioningViewModel): ViewModel
}