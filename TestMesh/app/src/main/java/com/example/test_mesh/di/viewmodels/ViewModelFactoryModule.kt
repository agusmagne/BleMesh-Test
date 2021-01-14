package com.example.test_mesh.di.viewmodels

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactoryProviders): ViewModelProvider.Factory
}