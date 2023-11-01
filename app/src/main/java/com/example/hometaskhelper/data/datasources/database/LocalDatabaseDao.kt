package com.example.hometaskhelper.data.datasources.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.TempTask
import com.example.hometaskhelper.data.datasources.database.entities.User
import com.example.hometaskhelper.ui.models.ModelSubject
import com.example.hometaskhelper.ui.models.ModelTask
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDatabaseDao {

    // TRANSACTIONS
    @Transaction
    suspend fun transactionInsertSubjectsInsertTasks(subjects: List<Subject>, tasks: List<Task>) {
        val cache = mutableMapOf<Int, Int>()
        var newId: Long
        for (subject in subjects) {
            newId = insertSubject(subject.copy(id = 0))
            cache[subject.id] = newId.toInt()
        }
        insertTasks(tasks.map { it.copy(subjectId = cache[it.subjectId] ?: 1) })
    }

    @Transaction
    suspend fun selectAllTempTasksUpdateTaskIsDeletedDeleteAllTempTasksDeleteAllRedactingTasks() {
        val tempTasks = selectAllTempTasks()
        for (tempTask in tempTasks) {
            updateTask(tempTask.toTask().copy(isRedacting = false))
        }
        updateTasksIsDeleted()
        deleteAllTempTasks()
        deleteAllRedactingTasks()
    }

    @Transaction
    suspend fun deleteDeletedTasksUpdateTasksIsRedactingDeleteAllTempTasks() {
        deleteDeletedTasks()
        updateTasksIsRedacting()
        deleteAllTempTasks()
    }

    @Transaction
    suspend fun updateSubjectNameUpdateTask(subjectId: Int, subjectName: String, task: Task) {
        updateSubjectName(subjectId, subjectName)
        updateTask(task)
    }

    // SELECT
    @Query("SELECT finished_tasks FROM ${User.TABLE_NAME} WHERE id = :id")
    suspend fun selectUserFinishedTasks(id: Int): String

    @Query("SELECT * FROM ${Subject.TABLE_NAME} WHERE id = :id")
    suspend fun selectSubjectById(id: Int): Subject

    @Query("SELECT subject_name FROM ${Subject.TABLE_NAME} WHERE id = :id")
    suspend fun selectSubjectNameById(id: Int): String

    @Query("SELECT * FROM ${Subject.TABLE_NAME} WHERE id = :id")
    suspend fun selectSubjectByName(id: Int): Subject

    @Query("SELECT * FROM ${Task.TABLE_NAME} WHERE id = :id")
    suspend fun selectTaskById(id: Int): Task

    @Query("SELECT * FROM ${Task.TABLE_NAME} WHERE subject_id = :subjectId")
    suspend fun selectTasksOfSubject(subjectId: Int): List<Task>

    @Query("SELECT * FROM ${Task.TABLE_NAME}")
    fun selectAllTasks(): Flow<List<ModelTask>>

    @Query("SELECT * FROM ${Subject.TABLE_NAME}")
    fun selectAllSubjects(): Flow<List<ModelSubject>>

    @Query("SELECT * FROM ${TempTask.TABLE_NAME}")
    suspend fun selectAllTempTasks(): List<TempTask>

    @Insert
    suspend fun insertToTempTasks(tasks: List<TempTask>)

    // INSERT
    @Insert
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Insert
    suspend fun insertTempTask(tempTask: TempTask)

    // UPDATE
    @Update
    suspend fun updateUser(user: User)

    @Update
    suspend fun updateSubject(subject: Subject)

    @Update
    suspend fun updateTask(task: Task)

    @Update
    suspend fun updateTempTasK(tempTask: TempTask)

    @Query("UPDATE ${Task.TABLE_NAME} SET is_redacting = 0")
    suspend fun updateTasksIsRedacting()

    @Query("UPDATE ${Task.TABLE_NAME} SET is_deleted = 0")
    suspend fun updateTasksIsDeleted()

    @Query("UPDATE ${Subject.TABLE_NAME} SET subject_name = :subjectName WHERE id = :subjectId")
    suspend fun updateSubjectName(subjectId: Int, subjectName: String)

    // DELETE
    @Delete
    suspend fun deleteUser(user: User)

    @Delete
    suspend fun deleteSubject(subject: Subject)

    @Delete
    suspend fun deleteTask(task: Task)

    @Delete
    suspend fun deleteTempTask(tempTask: TempTask)

    @Query("DELETE FROM ${TempTask.TABLE_NAME}")
    suspend fun deleteAllTempTasks()

    @Query("DELETE FROM ${Task.TABLE_NAME} WHERE is_redacting = 1")
    suspend fun deleteAllRedactingTasks()

    @Query("DELETE FROM ${Task.TABLE_NAME} WHERE is_deleted = 1")
    suspend fun deleteDeletedTasks()
}