package com.example.hometaskhelper.domain.network

import retrofit2.http.POST


interface SQLApi {
    @POST
    suspend fun createAuthTable()

    @POST
    suspend fun createTasksTable()

    @POST
    suspend fun createUsersTable()

}