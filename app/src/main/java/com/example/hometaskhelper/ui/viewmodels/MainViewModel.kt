package com.example.hometaskhelper.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hometaskhelper.MainApplication
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.repositories.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _tasksState = MutableStateFlow(emptyList<Task>())
    val tasksState: StateFlow<List<Task>> = _tasksState.asStateFlow()

    init {
        viewModelScope.launch {
            getAllTasks().collect {
                _tasksState.value = it
            }
        }
    }

    private fun getAllTasks(): Flow<List<Task>> {
        return repository.getAllTasks()
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