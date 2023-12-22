package com.capstoneapps.moka.data.pref
data class UserModel(
    val email: String,
    val accessToken: String?,
    val isLogin: Boolean = false,
    val timerData: TimerData = TimerData()
)

data class TimerData(
    val minutes: Long = 0,
    val seconds: Long = 0
)