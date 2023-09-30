package com.example.hometaskhelper

import android.app.Application
import com.example.hometaskhelper.di.AppComponent
import com.example.hometaskhelper.di.DaggerAppComponent

class MainApplication : Application() {

    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
    }

}
