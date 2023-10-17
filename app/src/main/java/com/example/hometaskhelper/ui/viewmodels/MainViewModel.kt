package com.example.hometaskhelper.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hometaskhelper.MainApplication
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.repositories.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _tasksState = MutableStateFlow(emptyList<Task>())
    val tasksState: StateFlow<List<Task>> = _tasksState.asStateFlow()

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
        coroutineScope.launch {
            repository.copyFromTasksToTempTasks()
        }
    }

    fun addTask(task: Task) {
        coroutineScope.launch {
            repository.addTask(task)
        }
    }

    fun deleteAllTempTasks() {
        coroutineScope.launch {
            repository.deleteAllTempTasks()
        }
    }

    fun getAllTasks(): Flow<List<Task>> {
        return repository.getAllTasks()
    }

    fun deleteTask(task: Task) {
        coroutineScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateTasks(tasks: List<Task>) {
        coroutineScope.launch {
            for (task in tasks) {
                repository.updateTask(task)
            }
        }
    }

    fun updateUserState(newState: UserState) {
        if (newState != _userState.value){
            _userState.value = newState
            when (newState) {
                UserState.DEFAULT -> deleteAllTempTasks()
                else -> tempSaveCurrentTasks()
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        deleteAllTempTasks()
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
