package com.example.hometaskhelper.di

import com.example.hometaskhelper.data.repositories.AppRepository
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    @Singleton
    fun repository(): AppRepository

}