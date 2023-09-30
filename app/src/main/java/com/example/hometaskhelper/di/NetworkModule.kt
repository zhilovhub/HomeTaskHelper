package com.example.hometaskhelper.di

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class NetworkModule {

    @Provides
    fun provideRetrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("")
            .build()
    }

}