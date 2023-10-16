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
interface DatabaseDao : SQLAccess {

    @Insert
    override suspend fun addUser(user: User)

    @Update
    override suspend fun updateUser(user: User)

    @Delete
    override suspend fun deleteUser(user: User)

    @Query("SELECT finished_tasks FROM ${User.TABLE_NAME} WHERE id = :id")
    override suspend fun getUserFinishedTasks(id: Int): String

    @Insert
    override suspend fun addSubject(subject: Subject)

    @Update
    override suspend fun updateSubject(subject: Subject)

    @Delete
    override suspend fun deleteSubject(subject: Subject)

    @Query("SELECT * FROM ${Subject.TABLE_NAME} WHERE id = :id")
    override suspend fun getSubjectById(id: Int): Subject

    @Query("SELECT subject_name FROM ${Subject.TABLE_NAME} WHERE id = :id")
    override suspend fun getSubjectNameById(id: Int): String

    @Query("SELECT * FROM ${Subject.TABLE_NAME} WHERE id = :id")
    override suspend fun getSubjectByName(id: Int): Subject

    @Insert
    override suspend fun addTask(task: Task)

    @Update
    override suspend fun updateTask(task: Task)

    @Delete
    override suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM ${Task.TABLE_NAME} WHERE id = :id")
    override suspend fun getTaskById(id: Int): Task

    @Query("SELECT * FROM ${Task.TABLE_NAME} WHERE subject_id = :subjectId")
    override suspend fun getTasksOfSubject(subjectId: Int): List<Task>

    @Query("SELECT * FROM ${Task.TABLE_NAME}")
    override fun getAllTasks(): Flow<List<Task>>

    @Insert
    override suspend fun addTempTask(tempTask: TempTask)

    @Update
    override suspend fun updateTempTasK(tempTask: TempTask)

    @Delete
    override suspend fun deleteTempTask(tempTask: TempTask)

    @Query("DELETE FROM ${TempTask.TABLE_NAME}")
    override suspend fun deleteAllTempTasks()

    @Query("SELECT * FROM ${TempTask.TABLE_NAME}")
    override fun getAllTempTasks(): Flow<List<TempTask>>

    @Query("INSERT INTO ${TempTask.TABLE_NAME} " +
            "(id, task_id, subject_id, description, to_date, is_redacting, is_finished) " +
            "SELECT null, id, subject_id, description, to_date, is_redacting, is_finished FROM ${Task.TABLE_NAME}")
    override suspend fun copyFromTasksToTempTasks()
}