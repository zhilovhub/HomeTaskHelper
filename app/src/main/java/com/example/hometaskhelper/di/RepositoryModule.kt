package com.example.hometaskhelper.di

import com.example.hometaskhelper.data.repositories.AppRepository
import com.example.hometaskhelper.data.datasources.database.LocalDatabaseDao
import com.example.hometaskhelper.data.datasources.network.SQLApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [DatabaseModule::class, NetworkModule::class])
class RepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(databaseDao: LocalDatabaseDao, sqlApi: SQLApi): AppRepository {
        return AppRepository(databaseDao, sqlApi)
    }
}