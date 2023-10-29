package com.example.hometaskhelper.di

import android.content.Context
import androidx.room.Room
import com.example.hometaskhelper.data.datasources.database.LocalDatabase
import com.example.hometaskhelper.data.datasources.database.LocalDatabaseDao
import com.example.hometaskhelper.data.datasources.database.HostDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DatabaseModule(val context: Context) {

    @Provides
    @Singleton
    fun provideAppDatabase(): LocalDatabase {
        return Room.databaseBuilder(
            context,
            LocalDatabase::class.java, "teletask_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAppDatabaseDao(database: LocalDatabase): LocalDatabaseDao {
        return database.databaseDao()
    }

    @Provides
    @Singleton
    fun provideTestNetworkDatabase(): HostDatabase {
        return Room.databaseBuilder(
            context,
            HostDatabase::class.java, "teletask_database_network"
        ).build()
    }
}