package com.example.hometaskhelper.domain.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class User(
    @PrimaryKey
    val id: Int
)