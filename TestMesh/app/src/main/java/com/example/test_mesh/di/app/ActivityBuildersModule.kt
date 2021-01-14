package com.example.test_mesh.di.app

import com.example.test_mesh.MainActivity
import com.example.test_mesh.di.ble.BleMeshManagerModule
import com.example.test_mesh.di.main.MainFragmentsBuildersModule
import com.example.test_mesh.di.main.MainModule
import com.example.test_mesh.di.main.MainViewModelsModule
import com.example.test_mesh.di.main.scanner.ProvisioningViewModelsModule
import com.example.test_mesh.view.scanner.ProvisioningActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = [
        MainFragmentsBuildersModule::class,
        MainViewModelsModule::class,
        MainModule::class
    ])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [
        ProvisioningViewModelsModule::class
    ])
    abstract fun contributeProvisioningActivity(): ProvisioningActivity


}