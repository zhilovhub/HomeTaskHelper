package com.example.hometaskhelper.data.datasources.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hometaskhelper.data.datasources.SQLAccess
import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.User

@Dao
interface DatabaseDao : SQLAccess {

    @Insert
    override suspend fun addUser(user: User)

    @Update
    override suspend fun updateUser(user: User)

    @Delete
    override suspend fun deleteUser(user: User)

    @Query("SELECT finished_tasks FROM User WHERE id = :id")
    override suspend fun getUserFinishedTasks(id: Int): String

    @Query("SELECT tg_id FROM User WHERE id = :id")
    override suspend fun getUserTgId(id: Int): Int

    @Insert
    override suspend fun addSubject(subject: Subject)

    @Update
    override suspend fun updateSubject(subject: Subject)

    @Delete
    override suspend fun deleteSubject(subject: Subject)

    @Query("SELECT * FROM Subject WHERE id = :id")
    override suspend fun getSubjectById(id: Int): Subject

    @Query("SELECT * FROM Subject WHERE id = :id")
    override suspend fun getSubjectByName(id: Int): Subject

    @Insert
    override suspend fun addTask(task: Task)

    @Update
    override suspend fun updateTask(task: Task)

    @Delete
    override suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM Task WHERE id = :id")
    override suspend fun getTaskById(id: Int): Task

    @Query("SELECT * FROM Task WHERE subject_id = :subjectId")
    override suspend fun getTasksOfSubject(subjectId: Int): List<Task>
}