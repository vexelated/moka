package com.capstoneapps.moka.ui.welcome

import androidx.lifecycle.ViewModel
import com.capstoneapps.moka.data.pref.UserModel
import com.capstoneapps.moka.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class WelcomeViewmodel (private val repository: UserRepository) : ViewModel() {
    fun getSession(): Flow<UserModel> {
        return repository.getSession()
    }
}