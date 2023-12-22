package com.capstoneapps.moka.data.repository

import com.capstoneapps.moka.data.api.ApiService
import com.capstoneapps.moka.data.pref.TimerData
import com.capstoneapps.moka.data.pref.UserModel
import com.capstoneapps.moka.data.pref.UserPreference
import com.capstoneapps.moka.data.response.CreateTaskResponse
import com.capstoneapps.moka.data.response.DeleteTaskResponse
import com.capstoneapps.moka.data.response.GetTaskResponse
import com.capstoneapps.moka.data.response.LoginResponse
import com.capstoneapps.moka.data.response.SignUpResponse
import com.capstoneapps.moka.data.response.UpdateTaskResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    suspend fun register(body: RequestBody): SignUpResponse {
        try {
            return apiService.register(body)
        } catch (e: Exception) {
            throw FailedException("Registration failed: ${e.message}")
        }
    }
    suspend fun login(body: RequestBody): LoginResponse {
        try {
            return apiService.login(body)
        } catch (e: Exception) {
            throw FailedException("Registration failed: ${e.message}")
        }
    }
    suspend fun creaTask(body: RequestBody): CreateTaskResponse{
        try{
            return apiService.createTask(body)
        } catch (e: Exception) {
            throw FailedException("create task failed: ${e.message}")
        }
    }
    suspend fun getTasksByStatus(status: Boolean): GetTaskResponse {
        try {
            return apiService.getTasksByStatus(status)
        } catch (e: Exception) {
            throw FailedException("Failed to get tasks by status: ${e.message}")
        }
    }
    suspend fun deleteTask(idTask: String): DeleteTaskResponse {
        try {
            return apiService.deleteTask(idTask)
        } catch (e: Exception) {
            throw FailedException("Delete task failed: ${e.message}")
        }
    }
    suspend fun updateTask(idtask: String,requestBody: RequestBody): UpdateTaskResponse {
        try {
            return apiService.updateTask(idtask,requestBody)
        } catch (e: Exception) {
            throw FailedException("Update task failed: ${e.message}")
        }
    }
    suspend fun logout() {
        userPreference.logout()
    }
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }
    suspend fun saveSession(user: UserModel) {
        return userPreference.saveSession(user)
    }
    fun getFocusTimerData(): Flow<TimerData> {
        return userPreference.getFocusTimerData()
    }

    fun getBreakTimerData(): Flow<TimerData> {
        return userPreference.getBreakTimerData()
    }

    suspend fun saveFocusTimerData(timerData: TimerData) {
        userPreference.saveFocusTimerData(timerData)
    }

    suspend fun saveBreakTimerData(timerData: TimerData) {
        userPreference.saveBreakTimerData(timerData)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}

class FailedException(message: String) : Exception(message)

