package com.capstoneapps.moka.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneapps.moka.data.repository.UserRepository

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    private val _countdownTime = MutableLiveData<Long>()
    private val _countdownTimeIstirahat = MutableLiveData<Long>()

    val countdownTime: LiveData<Long> get() = _countdownTime
    val countdownTimeIstirahat: LiveData<Long> get() = _countdownTimeIstirahat

    fun setCountdownTime(timeInMillis: Long) {
        _countdownTime.value = timeInMillis
    }

    fun setCountdownTimeIstirahat(timeInMillis: Long) {
        _countdownTimeIstirahat.value = timeInMillis
    }

    suspend fun logOut() {
        return repository.logout()
    }
}

