package com.example.hometaskhelper.di

import com.example.hometaskhelper.domain.AppRepository
import com.example.hometaskhelper.domain.database.DatabaseDao
import com.example.hometaskhelper.domain.network.SQLApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [DatabaseModule::class, NetworkModule::class])
class RepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(databaseDao: DatabaseDao, sqlApi: SQLApi): AppRepository {
        return AppRepository(databaseDao, sqlApi)
    }
}