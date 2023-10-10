package com.example.hometaskhelper

import android.app.Application
import com.example.hometaskhelper.data.DataSourceType
import com.example.hometaskhelper.di.AppComponent
import com.example.hometaskhelper.di.DaggerAppComponent
import com.example.hometaskhelper.di.DatabaseModule
import com.example.hometaskhelper.di.RepositoryModule

class MainApplication : Application() {

    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .databaseModule(DatabaseModule(applicationContext))
            .repositoryModule(RepositoryModule(DataSourceType.LocalDatabase))
            .build()
    }
}
