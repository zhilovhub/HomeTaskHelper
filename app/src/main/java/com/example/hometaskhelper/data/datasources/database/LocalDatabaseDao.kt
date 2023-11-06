package com.example.hometaskhelper.data.datasources.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.hometaskhelper.TASK_SHOULD_CHECK
import com.example.hometaskhelper.TASK_SHOULD_UPDATE
import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.User
import com.example.hometaskhelper.ui.models.ModelSubject
import com.example.hometaskhelper.ui.models.ModelTask
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDatabaseDao {

    // TRANSACTIONS
    @Transaction
    suspend fun cleanDb(state: String) {
        deleteCheckDeletedTasks()
        updateStates(state)
    }

    @Transaction
    suspend fun acceptRedacting(
        subjects: List<Subject>,
        tasks: List<Task>,
        tasksToDelete: List<Task>
    ) {
        transactionInsertSubjectsInsertTasks(subjects, tasks)
        deleteTasks(tasksToDelete)
        updateIsRedacting(false)
    }

    @Transaction
    suspend fun transactionInsertSubjectsInsertTasks(subjects: List<Subject>, tasks: List<Task>) {
        val cache = mutableMapOf<Int, Int>()
        var newId: Long
        for (subject in subjects) {
            newId = insertSubject(if (subject.id < 0) subject.copy(id = 0) else subject)
            if (subject.id < 0) {
                cache[subject.id] = newId.toInt()
            }
        }
        insertTasks(tasks.map { it.copy(subjectId = cache[it.subjectId] ?: it.subjectId) })
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

    // INSERT
    @Insert
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    // UPDATE
    @Update
    suspend fun updateUser(user: User)

    @Update
    suspend fun updateSubject(subject: Subject)

    @Update
    suspend fun updateTask(task: Task)

    @Query("UPDATE ${Task.TABLE_NAME} SET is_redacting = :isRedacting")
    suspend fun updateIsRedacting(isRedacting: Boolean)

    @Query("UPDATE ${Subject.TABLE_NAME} SET subject_name = :subjectName WHERE id = :subjectId")
    suspend fun updateSubjectName(subjectId: Int, subjectName: String)

    @Query("UPDATE ${Task.TABLE_NAME} SET local_id = id")
    suspend fun updateLocalIds()

    @Query("UPDATE ${Task.TABLE_NAME} SET state = null WHERE state = :state")
    suspend fun updateStates(state: String)

    // DELETE
    @Delete
    suspend fun deleteTasks(tasks: List<Task>)

    @Query("DELETE FROM ${Task.TABLE_NAME} WHERE is_redacting = 1")
    suspend fun deleteAllRedactingTasks()

    @Query("DELETE FROM ${Task.TABLE_NAME} WHERE is_deleted = 1 and state = '$TASK_SHOULD_CHECK'")
    suspend fun deleteCheckDeletedTasks()
}