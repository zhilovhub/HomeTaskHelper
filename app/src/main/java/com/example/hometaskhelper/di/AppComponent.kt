package com.example.hometaskhelper.di

import dagger.Component
import retrofit2.Retrofit

@Component
interface AppComponent {

    fun getSQLApi(): Retrofit

}