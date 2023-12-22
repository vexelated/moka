package com.capstoneapps.moka.ui.detail

import androidx.lifecycle.ViewModel
import com.capstoneapps.moka.data.repository.UserRepository
import com.capstoneapps.moka.data.response.CreateTaskResponse
import okhttp3.RequestBody

class DetailViewmodel (private val userRepository: UserRepository) : ViewModel() {
    suspend fun creaTask(body: RequestBody): CreateTaskResponse {
        return userRepository.creaTask(body)
    }

}