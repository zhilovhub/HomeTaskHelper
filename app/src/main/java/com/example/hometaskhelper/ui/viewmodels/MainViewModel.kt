package com.example.hometaskhelper.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hometaskhelper.MainApplication
import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.repositories.AppRepository
import com.example.hometaskhelper.ui.models.ModelSubject
import com.example.hometaskhelper.ui.models.ModelTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _tasksState = MutableStateFlow(TasksUiState(tasks = listOf(), subjects = mapOf()))
    val tasksState: StateFlow<TasksUiState> = _tasksState.asStateFlow()

    private val _userState = MutableStateFlow(UserState.DEFAULT)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    init {
        coroutineScope.launch {
            repository.getAllTasks().collect { newTasks ->
                println(newTasks.toString())
                _tasksState.update { tasksUiState -> tasksUiState.copy(tasks = newTasks) }
            }
        }
        coroutineScope.launch {
            repository.getAllSubjects().collect {newSubjects ->
                println(newSubjects.toString())
                _tasksState.update { tasksUiState -> tasksUiState.copy(subjects = mapOf(*newSubjects.map { it.id to it }.toTypedArray())) }
            }
        }
    }

    private fun tempSaveCurrentTasks() {
        coroutineScope.launch { repository.insertToTempTasks(_tasksState.value.tasks.map { it.toTempTask() }) }
    }

    fun addNewTask() {
        var taskId = -1
        var subjectId = -1
        while (_tasksState.value.subjects.containsKey(subjectId)) {
            subjectId--;
        }
        while (_tasksState.value.tasks.map { it.id }.contains(taskId)) {
            taskId--;
        }
        val newSubject = ModelSubject(
            id = subjectId,
            subjectName = "Новый",
            aliases = ""
        )
        val newTask = ModelTask(
            id = taskId,
            subjectId = subjectId,
            description = "",
            toDate = "",
            isRedacting = true,
            isFinished = false,
            isDeleted = false
        )
        _tasksState.update {
            _tasksState.value.copy(
                tasks = (_tasksState.value.tasks.toMutableList() + newTask).toList(),
                subjects = (_tasksState.value.subjects.toMutableMap().plus(subjectId to newSubject)).toMap()
            )
        }
    }

    fun cancelRedacting() {
        coroutineScope.launch { repository.cancelRedacting() }
    }

    fun acceptRedacting(tasks: List<ModelTask>, changeLocalIsRedacting: (Int) -> Unit) {
        coroutineScope.launch {
            for (task in tasks) {
                if (task.isRedacting) {
                    repository.updateSubjectNameAndTask(
                        task.subjectId,
                        _tasksState.value.subjects[task.subjectId]?.subjectName ?: "",
                        task.toTask().copy(isRedacting = false)
                    )
                    withContext(Dispatchers.Main) {
                        changeLocalIsRedacting(task.id)
                    }
                }
            }
            repository.cleanForAcceptRedacting()
        }
    }

    fun deleteTask(task: ModelTask) {
        val newTasks = _tasksState.value.tasks.toMutableList()
        val index = newTasks.indexOf(task)
        newTasks[index] = task.copy(isDeleted = true)
        _tasksState.update {
            _tasksState.value.copy(tasks = newTasks)
        }
    }

    fun updateUserState(newState: UserState) {
        if (newState != _userState.value){
            _userState.value = newState
            if (_userState.value != UserState.DEFAULT) {
                tempSaveCurrentTasks()
            }
        }
    }

    fun updateSubjectName(subjectId: Int, subjectName: String) {
        val newSubjects = _tasksState.value.subjects.toMutableMap()
        if (newSubjects.containsKey(subjectId)) {
            newSubjects[subjectId] = newSubjects[subjectId]?.copy(subjectName = subjectName) ?: ModelSubject()
        }
        _tasksState.update {
            _tasksState.value.copy(
                subjects = newSubjects.toMap()
            )
        }
    }

    fun updateTask(task: Task) {
        coroutineScope.launch { repository.updateTask(task) }
    }
    
    override fun onCleared() {
        super.onCleared()
        cancelRedacting()
        coroutineScope.cancel()
    }

    companion object {
        fun factory(context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (context.applicationContext as MainApplication).appComponent.repository()
                return MainViewModel(repository) as T
            }
        }
    }
}

enum class UserState {
    DEFAULT,
    REDACTING,
    DELETING
}

data class TasksUiState(
    val tasks: List<ModelTask>,
    val subjects: Map<Int, ModelSubject>
)