package com.example.hometaskhelper.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hometaskhelper.MainApplication
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.repositories.AppRepository
import com.example.hometaskhelper.ui.models.ModelTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _tasksState = MutableStateFlow(emptyList<ModelTask>())
    val tasksState: StateFlow<List<ModelTask>> = _tasksState.asStateFlow()

    private val _userState = MutableStateFlow(UserState.DEFAULT)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    init {
        coroutineScope.launch {
            getAllTasks().collect {
                _tasksState.value = it
            }
        }
    }

    fun tempSaveCurrentTasks() {
        coroutineScope.launch { repository.copyFromTasksToTempTasks() }
    }

    fun addNewTask() {
        coroutineScope.launch { repository.addNewTask() }
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
                        task.subjectName,
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

    fun getAllTasks(): Flow<List<ModelTask>> {
        return repository.getAllTasks()
    }

    fun deleteTask(task: ModelTask) {
        coroutineScope.launch { repository.updateTask(task.toTask().copy(isDeleted = true)) }
    }

    fun updateUserState(newState: UserState) {
        if (newState != _userState.value){
            _userState.value = newState
            if (_userState.value != UserState.DEFAULT) {
                tempSaveCurrentTasks()
            }
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
