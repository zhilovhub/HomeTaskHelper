package com.example.hometaskhelper.data.datasources

import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.TempTask
import com.example.hometaskhelper.data.datasources.database.entities.User
import kotlinx.coroutines.flow.Flow

interface SQLAccess {

    suspend fun addUser(user: User)

    suspend fun updateUser(user: User)

    suspend fun deleteUser(user: User)

    suspend fun getUserFinishedTasks(id: Int): String

    suspend fun addSubject(subject: Subject)

    suspend fun updateSubject(subject: Subject)

    suspend fun deleteSubject(subject: Subject)

    suspend fun getSubjectById(id: Int): Subject

    suspend fun getSubjectNameById(id: Int): String

    suspend fun getSubjectByName(id: Int): Subject

    suspend fun addTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun getTaskById(id: Int): Task

    suspend fun getTasksOfSubject(subjectId: Int): List<Task>

    fun getAllTasks(): Flow<List<Task>>

    suspend fun addTempTask(tempTask: TempTask)

    suspend fun updateTempTasK(tempTask: TempTask)

    suspend fun deleteTempTask(tempTask: TempTask)

    suspend fun deleteAllTempTasks()

    fun getAllTempTasks(): Flow<List<TempTask>>

    suspend fun copyFromTasksToTempTasks()

}