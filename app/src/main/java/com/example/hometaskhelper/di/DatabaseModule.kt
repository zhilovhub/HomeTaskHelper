package com.example.hometaskhelper.di

import android.content.Context
import androidx.room.Room
import com.example.hometaskhelper.data.datasources.database.AppDatabase
import com.example.hometaskhelper.data.datasources.database.DatabaseDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DatabaseModule(val context: Context) {

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "teletask_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAppDatabaseDao(database: AppDatabase): DatabaseDao {
        return database.databaseDao()
    }

}