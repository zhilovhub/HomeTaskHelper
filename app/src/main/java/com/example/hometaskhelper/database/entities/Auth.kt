package com.example.hometaskhelper.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Auth(
    @PrimaryKey
    val id: Int
)