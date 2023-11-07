package com.example.hometaskhelper.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hometaskhelper.MainApplication
import com.example.hometaskhelper.TASK_SHOULD_CHECK
import com.example.hometaskhelper.TASK_SHOULD_UPDATE
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

    private var tempTasks = listOf<ModelTask>()
    private var tempSubjects = mapOf<Int, ModelSubject>()  // TODO there is a bug, fix later

    private val _userState = MutableStateFlow(UserState.DEFAULT)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private var accepting: Boolean = false

    private val _hasAuthorized = MutableStateFlow(false)
    val hasAuthorized: StateFlow<Boolean> = _hasAuthorized.asStateFlow()

    init {
        coroutineScope.launch {
            repository.getAllTasks().collect { newTasks ->
                _tasksState.update { tasksUiState ->
                    tasksUiState.copy(tasks = mergeIncomingChangesWithLocal(newTasks))
                }
                repository.cleanDb(state = TASK_SHOULD_UPDATE)
            }
        }
        coroutineScope.launch {
            repository.getAllSubjects().collect {newSubjects ->
                _tasksState.update { tasksUiState -> tasksUiState.copy(subjects = mapOf(*newSubjects.map { it.id to it }.toTypedArray())) }
            }
        }
    }

    private fun tempSaveCurrentTasks() {
        tempTasks = _tasksState.value.tasks.toList()
        tempSubjects = _tasksState.value.subjects.toMap()
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
            isDeleted = false,
            state = null
        )
        _tasksState.update {
            _tasksState.value.copy(
                tasks = (_tasksState.value.tasks.toMutableList() + newTask).toList(),
                subjects = (_tasksState.value.subjects.toMutableMap().plus(subjectId to newSubject)).toMap()
            )
        }
    }

    fun cancelRedacting() {
        _tasksState.update { _tasksState.value.copy(tasks = tempTasks, subjects = tempSubjects) }
        resetIsRedactingTasks()
    }

    fun resetIsRedactingTasks() {
        coroutineScope.launch { repository.updateIsRedacting(false) }
    }

    fun resetCheckTasks() {
        coroutineScope.launch { repository.cleanDb(state = TASK_SHOULD_CHECK)}
    }

    fun acceptRedacting() {  // TODO clear local tasks and collect them again
        accepting = true
        val getTask = {task: Task ->
            if (task.id < 0) task.copy(id = 0)
            else task
        }
        val tasks = _tasksState.value.tasks

        coroutineScope.launch {
            repository.acceptRedacting(
                subjects = _tasksState.value.subjects.map { it.value.toSubject() },
                tasks = tasks.map { getTask(it.toTask()) },
                tasksToDelete = tasks.filter { it.isDeleted }.map { it.toTask() }
            )
        }
    }

    fun deleteTask(task: ModelTask) {
        val newTasks = _tasksState.value.tasks.toMutableList()
        val index = newTasks.indexOfFirst { it.id == task.id }
        newTasks[index] = task.copy(isDeleted = true)
        updateTaskIsRedacting(task)
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

    fun updateTaskDescription(task: ModelTask, description: String) {
        val newTasks = _tasksState.value.tasks.toMutableList()
        val index = newTasks.indexOfFirst { it.id == task.id }
        updateTaskIsRedacting(task)
        newTasks[index] = task.copy(description = description)
        _tasksState.update {
            _tasksState.value.copy(
                tasks = newTasks.toList()
            )
        }
    }

    fun updateSubjectName(task: ModelTask, subjectId: Int, subjectName: String) {
        val newSubjects = _tasksState.value.subjects.toMutableMap()
        if (newSubjects.containsKey(subjectId)) {
            newSubjects[subjectId] = newSubjects[subjectId]?.copy(subjectName = subjectName) ?: ModelSubject()
        }
        updateTaskIsRedacting(task)
        _tasksState.update {
            _tasksState.value.copy(
                subjects = newSubjects.toMap()
            )
        }
    }

    fun updateTaskIsFinished(task: ModelTask, isFinished: Boolean) {
        val newTasks = _tasksState.value.tasks.toMutableList()
        val index = newTasks.indexOfFirst { it.id == task.id }
        newTasks[index] = task.copy(isFinished = isFinished)
        _tasksState.update {
            _tasksState.value.copy(
                tasks = newTasks.toList()
            )
        }
    }

    private fun updateTaskIsRedacting(task: ModelTask) {
        if (!task.isRedacting) {
            coroutineScope.launch {
                repository.updateTask(task.copy(isRedacting = true, state = TASK_SHOULD_UPDATE).toTask())
            }
        }
    }

    private fun mergeIncomingChangesWithLocal(incomingTasks: List<ModelTask>): List<ModelTask> {  // TODO transform this func to Network. Now it just for one people
        if (accepting) {
            accepting = false
            return incomingTasks
        }

        val newTasks = _tasksState.value.tasks.toMutableList()
        val newTasksIndexes = _tasksState.value.tasks.map { it.id }.toMutableList()

        var incomingTask: ModelTask
        var incomingId: Int
        var incomingTaskState: String?

        for (index in incomingTasks.indices) {
            incomingTask = incomingTasks[index]
            incomingId = incomingTask.id
            incomingTaskState = incomingTask.state

            when (incomingTaskState) {  // Not my task
                TASK_SHOULD_CHECK -> {
                    if (incomingTask.isDeleted) {  // Delete not my task
                        newTasks.removeIf { it.id == incomingId }
                    } else if (newTasksIndexes.contains(incomingId)) {  // Update not my task
                        newTasks.replaceAll {
                            if (it.id == incomingId)
                                incomingTask.copy(
                                    isRedacting = false, isDeleted = false, state = null
                                )
                            else it
                        }
                    } else if (!newTasksIndexes.contains(incomingId)) {  // Add not my task
                        newTasks.add(index, incomingTask.copy(
                            isRedacting = false, isDeleted = false, state = null
                        ))
                    }
                }
                TASK_SHOULD_UPDATE -> {  // Should update my task in local
                    Log.d("HomeScreen", incomingTask.toString())
                    newTasks.replaceAll {
                        if (it.id == incomingId) it.copy(
                            subjectId = incomingTask.subjectId,
                            isRedacting = incomingTask.isRedacting,
                            state = null
                        )
                        else it
                    }
                }
                else -> if (!newTasksIndexes.contains(incomingId)) newTasks.add(incomingTask)
            }
            newTasksIndexes.remove(incomingId)
        }

        for (task in newTasks.toList()) {
            if (newTasksIndexes.contains(task.id)) {
                newTasks.remove(task)
            }
        }

        tempTasks = newTasks.filter { !it.isRedacting }

        return newTasks
    }
    
    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    companion object {
        fun factory(context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (context as MainApplication).appComponent.repository()
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