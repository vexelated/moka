package com.capstoneapps.moka.ui.note

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneapps.moka.data.repository.UserRepository
import com.capstoneapps.moka.data.response.DeleteTaskResponse
import com.capstoneapps.moka.data.response.GetTaskResponse
import com.capstoneapps.moka.data.response.UpdateTaskResponse
import okhttp3.RequestBody

class NoteViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun deleteTask(idTask: String): DeleteTaskResponse {
        val response = userRepository.deleteTask(idTask)
        // Fetch updated tasks after deletion
        getTasksByStatus()
        return response
    }
    private val _tasksLiveData = MutableLiveData<GetTaskResponse?>()
    val tasksLiveData: MutableLiveData<GetTaskResponse?> get() = _tasksLiveData
    suspend fun getTasksByStatus() {
        try {
            val response = userRepository.getTasksByStatus(false)
            _tasksLiveData.value = response
        } catch (e: Exception) {
            // Handle the exception, log it, and set LiveData value to null
            _tasksLiveData.value = null
        }
    }
    suspend fun getTasksByStatusHistory() {
        try {
            val response = userRepository.getTasksByStatus(true)
            _tasksLiveData.value = response
        } catch (e: Exception) {
            // Handle the exception, log it, and set LiveData value to null
            _tasksLiveData.value = null
        }
    }
    suspend fun updateTask(idtask: String,requestBody: RequestBody): UpdateTaskResponse {
        return try {
            userRepository.updateTask(idtask,requestBody)
        } catch (e: Exception) {
            // Handle the exception, log it, and return null or a default response
            UpdateTaskResponse(message = "Update task failed: ${e.message}")
        }
    }
}
