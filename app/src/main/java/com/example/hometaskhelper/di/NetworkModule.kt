package com.example.hometaskhelper.di

import com.example.hometaskhelper.domain.network.SQLApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("")
            .build()
    }

    @Provides
    @Singleton
    fun provideSQLApi(retrofit: Retrofit): SQLApi {
        return retrofit.create(SQLApi::class.java)
    }

}