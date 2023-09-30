package com.example.hometaskhelper.di

import com.example.hometaskhelper.network.SQLApi
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    @Singleton
    fun SQLApi(): SQLApi

}