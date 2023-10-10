package com.example.hometaskhelper.data.datasources.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Subject(
    @PrimaryKey
    val id: Int,
    val name: String
)