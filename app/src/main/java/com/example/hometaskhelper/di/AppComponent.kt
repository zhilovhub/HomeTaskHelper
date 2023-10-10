package com.example.hometaskhelper.di

import com.example.hometaskhelper.domain.database.DatabaseDao
import com.example.hometaskhelper.domain.network.SQLApi
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    @Singleton
    fun SQLApi(): SQLApi

    @Singleton
    fun databaseDao(): DatabaseDao

}