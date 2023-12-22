package com.capstoneapps.moka.ui.login

import androidx.lifecycle.ViewModel
import com.capstoneapps.moka.data.pref.UserModel
import com.capstoneapps.moka.data.repository.UserRepository
import com.capstoneapps.moka.data.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    suspend fun login(body: RequestBody): LoginResponse {
        return repository.login(body)
    }
    suspend fun saveSession(user: UserModel) {
        return repository.saveSession(user)
    }
}