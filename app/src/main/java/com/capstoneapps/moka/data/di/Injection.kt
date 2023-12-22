package com.capstoneapps.moka.data.di

import android.content.Context
import com.capstoneapps.moka.data.api.ApiConfig
import com.capstoneapps.moka.data.pref.UserPreference
import com.capstoneapps.moka.data.pref.dataStore
import com.capstoneapps.moka.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }

        // Set token setelah mendapatkan sesi pengguna
        ApiConfig.setAuthToken(user.accessToken?: "")

        // Panggil fungsi getApiService tanpa parameter token
        val apiService = ApiConfig.getApiService()

        return UserRepository.getInstance(pref, apiService)
    }
}