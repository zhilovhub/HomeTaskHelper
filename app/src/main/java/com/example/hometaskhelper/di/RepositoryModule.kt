package com.example.hometaskhelper.di

import com.example.hometaskhelper.data.repositories.DataSourceType
import com.example.hometaskhelper.data.repositories.AppRepository
import com.example.hometaskhelper.data.datasources.database.DatabaseDao
import com.example.hometaskhelper.data.datasources.network.SQLApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [DatabaseModule::class, NetworkModule::class])
class RepositoryModule(private val dataSourceType: DataSourceType) {
    @Provides
    @Singleton
    fun provideRepository(databaseDao: DatabaseDao, sqlApi: SQLApi): AppRepository {
        return AppRepository(databaseDao, sqlApi, dataSourceType)
    }
}