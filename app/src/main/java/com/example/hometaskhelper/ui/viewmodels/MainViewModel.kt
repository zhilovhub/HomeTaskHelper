package com.example.hometaskhelper.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hometaskhelper.MainApplication
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.TempTask
import com.example.hometaskhelper.data.repositories.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: AppRepository
) : ViewModel() {

//    private val _uiState = MutableStateFlow(UiState.Tasks(emptyList()))
//    val uiState: StateFlow<UiState> =_uiState.asStateFlow()

    private val _tasksState = MutableStateFlow(emptyList<Task>())
    val tasksState: StateFlow<List<Task>> = _tasksState.asStateFlow()
//
    private val _userState = MutableStateFlow(UserState.DEFAULT)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    init {
        viewModelScope.launch {
            getAllTasks().collect {
                _tasksState.value = it
            }
//            getAllTempTasks().collect {
//                if (_userState.value != UserState.DEFAULT) {
//                    _uiState.value = UiState.TempTasks(it)
//                }
//            }
            // НАДО ЧТОБЫ МОЖНО БЫЛО КОПИРОВАТЬ ЗАДАНИЯ ИЗ ОДНОГО В ДРУГОЕ
        }
    }

    fun addTempTask(tempTask: TempTask) {
        viewModelScope.launch {
            repository.addTempTask(tempTask)
        }
    }

    fun deleteAllTempTasks() {
        viewModelScope.launch {
            repository.deleteAllTempTasks()
        }
    }

    fun getAllTasks(): Flow<List<Task>> {
        return repository.getAllTasks()
    }

    fun getAllTempTasks(): Flow<List<TempTask>> {
        return repository.getAllTempTasks()
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateTasks(tasks: List<Task>) {
        viewModelScope.launch {
            for (task in tasks) {
                repository.updateTask(task)
            }
        }
    }

    fun updateUserState(newState: UserState) {
        _userState.value = newState
    }

    override fun onCleared() {
        super.onCleared()
//        deleteAllTempTasks()
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
