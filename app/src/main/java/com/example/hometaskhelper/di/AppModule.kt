package com.example.hometaskhelper.di

import dagger.Module

@Module(includes = [NetworkModule::class, DatabaseModule::class])
class AppModule {
}