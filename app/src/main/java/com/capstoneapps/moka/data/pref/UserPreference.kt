package com.capstoneapps.moka.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.accessToken?: ""
            preferences[IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }
    suspend fun saveFocusTimerData(timerData: TimerData) {
        dataStore.edit { preferences ->
            preferences[FOCUS_MINUTES_KEY] = timerData.minutes
            preferences[FOCUS_SECONDS_KEY] = timerData.seconds
        }
    }

    suspend fun saveBreakTimerData(timerData: TimerData) {
        dataStore.edit { preferences ->
            preferences[BREAK_MINUTES_KEY] = timerData.minutes
            preferences[BREAK_SECONDS_KEY] = timerData.seconds
        }
    }

    fun getFocusTimerData(): Flow<TimerData> {
        return dataStore.data.map { preferences ->
            TimerData(
                minutes = preferences[FOCUS_MINUTES_KEY] ?: 0,
                seconds = preferences[FOCUS_SECONDS_KEY] ?: 0
            )
        }
    }

    fun getBreakTimerData(): Flow<TimerData> {
        return dataStore.data.map { preferences ->
            TimerData(
                minutes = preferences[BREAK_MINUTES_KEY] ?: 0,
                seconds = preferences[BREAK_SECONDS_KEY] ?: 0
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val FOCUS_MINUTES_KEY = longPreferencesKey("focus_minutes")
        private val FOCUS_SECONDS_KEY = longPreferencesKey("focus_seconds")
        private val BREAK_MINUTES_KEY = longPreferencesKey("break_minutes")
        private val BREAK_SECONDS_KEY = longPreferencesKey("break_seconds")


        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}