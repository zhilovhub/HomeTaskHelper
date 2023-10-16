package com.example.hometaskhelper.data.datasources.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hometaskhelper.data.datasources.SQLAccess
import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.TempTask
import com.example.hometaskhelper.data.datasources.database.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface TestNetworkDao {

    @Insert
    suspend fun addUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT finished_tasks FROM ${User.TABLE_NAME} WHERE id = :id")
    suspend fun getUserFinishedTasks(id: Int): String

    @Insert
    suspend fun addSubject(subject: Subject)

    @Update
    suspend fun updateSubject(subject: Subject)

    @Delete
    suspend fun deleteSubject(subject: Subject)

    @Query("SELECT * FROM ${Subject.TABLE_NAME} WHERE id = :id")
    suspend fun getSubjectById(id: Int): Subject

    @Query("SELECT subject_name FROM ${Subject.TABLE_NAME} WHERE id = :id")
    suspend fun getSubjectNameById(id: Int): String

    @Query("SELECT * FROM ${Subject.TABLE_NAME} WHERE id = :id")
    suspend fun getSubjectByName(id: Int): Subject

    @Insert
    suspend fun addTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM ${Task.TABLE_NAME} WHERE id = :id")
    suspend fun getTaskById(id: Int): Task

    @Query("SELECT * FROM ${Task.TABLE_NAME} WHERE subject_id = :subjectId")
    suspend fun getTasksOfSubject(subjectId: Int): List<Task>

    @Query("SELECT * FROM ${Task.TABLE_NAME}")
    fun getAllTasks(): Flow<List<Task>>

}