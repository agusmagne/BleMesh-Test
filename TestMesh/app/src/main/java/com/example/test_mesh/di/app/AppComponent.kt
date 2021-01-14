package com.example.test_mesh.di.app

import android.app.Application
import com.example.test_mesh.BaseApplication
import com.example.test_mesh.di.ble.BleMeshManagerModule
import com.example.test_mesh.di.viewmodels.ViewModelFactoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ViewModelFactoryModule::class,
        ActivityBuildersModule::class,
        BleMeshManagerModule::class
    ]
)
interface AppComponent : AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

}