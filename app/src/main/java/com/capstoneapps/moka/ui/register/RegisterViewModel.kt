package com.capstoneapps.moka.ui.register


import androidx.lifecycle.ViewModel
import com.capstoneapps.moka.data.repository.UserRepository
import com.capstoneapps.moka.data.response.SignUpResponse
import okhttp3.RequestBody

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun register(body: RequestBody): SignUpResponse {
        return userRepository.register(body)
    }
}

