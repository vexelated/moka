package com.capstoneapps.moka.ui.main

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.capstoneapps.moka.R

class CountdownService : Service() {

    private lateinit var countDownTimer: CountDownTimer
    private var isPaused = false
    private var remainingTime: Long = 0
    private var isBreakTimer = false

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate() {
        super.onCreate()

        // Create the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Countdown Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE_TIMER -> pauseTimer()
            ACTION_RESUME_TIMER -> resumeTimer()
            else -> {
                val totalTimeInMillis = intent?.getLongExtra(TOTAL_TIME, 0) ?: 0
                val totalTimeInMillisBreak = intent?.getLongExtra(TOTAL_TIMEBREAK, 0) ?: 0

                // Adding notification for Foreground Service
                val notification = createNotification("Countdown is running")
                startForeground(NOTIFICATION_ID, notification)

                startCountdown(totalTimeInMillis, totalTimeInMillisBreak)
            }
        }
        return START_NOT_STICKY
    }

    private fun startCountdown(totalTimeInMillis: Long, totalTimeInMillisBreak: Long) {
        isBreakTimer = false
        countDownTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isPaused) {
                    remainingTime = millisUntilFinished
                    sendCountdownUpdate(millisUntilFinished)
                }
            }

            override fun onFinish() {
                if (totalTimeInMillisBreak > 0) {
                    startCountdownIstirahat(totalTimeInMillisBreak)
                } else {
                    sendCountdownFinish()
                    stopSelf() // Stop the service when countdown finishes
                }
            }
        }
        countDownTimer.start()
    }

    private fun startCountdownIstirahat(totalTimeInMillisBreak: Long) {
        isBreakTimer = true
        countDownTimer = object : CountDownTimer(totalTimeInMillisBreak, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isPaused) {
                    remainingTime = millisUntilFinished
                    sendCountdownUpdateBreak(millisUntilFinished)
                }
            }

            override fun onFinish() {
                sendCountdownFinish()
                stopSelf() // Stop the service when countdown finishes
            }
        }
        countDownTimer.start()
    }


    private fun sendBroadcastWithPendingIntent(intent: Intent, action: String) {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        updateNotification(pendingIntent)
    }

    private fun sendCountdownUpdate(millisUntilFinished: Long) {
        val intent = Intent(COUNTDOWN_UPDATE)
        intent.putExtra(COUNTDOWN_TIME, millisUntilFinished)
        intent.putExtra(COUNTDOWN_START_BREAK, isBreakTimer) // Add the flag to the intent
        sendBroadcastWithPendingIntent(intent, COUNTDOWN_UPDATE)
    }
    private fun sendCountdownUpdateBreak(millisUntilFinished: Long) {
        val intent = Intent(COUNTDOWN_UPDATE_BREAK)
        intent.putExtra(COUNTDOWN_TIME_BREAK, millisUntilFinished)
        sendBroadcastWithPendingIntent(intent, COUNTDOWN_UPDATE_BREAK)
    }
    private fun sendCountdownFinish() {
        val intent = Intent(COUNTDOWN_FINISH)
        sendBroadcastWithPendingIntent(intent, COUNTDOWN_FINISH)
    }

    private fun updateNotification(pendingIntent: PendingIntent) {
        val notification = createNotification("Countdown is running", pendingIntent)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    // Modify createNotification to accept a PendingIntent parameter
    private fun createNotification(contentText: String, pendingIntent: PendingIntent): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Countdown Service")
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent) // Set the PendingIntent here
            .build()
    }

    private fun createNotification(contentText: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Countdown Service")
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent) // Set the PendingIntent here
            .build()
    }
    private fun pauseTimer() {
        isPaused = true
        countDownTimer.cancel()
    }

    // Fungsi untuk melanjutkan timer dari waktu yang tersisa
// Fungsi untuk melanjutkan timer dari waktu yang tersisa
    private fun resumeTimer() {
        isPaused = false
        if (isBreakTimer) {
            startCountdownIstirahat(remainingTime)
        } else {
            startCountdown(remainingTime, 0)
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val COUNTDOWN_START_BREAK = "countdown_start_break"
        const val TOTAL_TIME = "total_time"
        const val TOTAL_TIMEBREAK = "total_time_break"  // New constant for break time
        const val ACTION_PAUSE_TIMER = "pause_timer"
        const val ACTION_RESUME_TIMER = "resume_timer"
        const val COUNTDOWN_UPDATE = "countdown_update"
        const val COUNTDOWN_UPDATE_BREAK = "countdown_update_break"
        const val COUNTDOWN_FINISH = "countdown_finish"
        const val COUNTDOWN_TIME = "countdown_time"
        const val COUNTDOWN_TIME_BREAK = "countdown_time_break"
        private const val CHANNEL_ID = "CountdownServiceChannel"
        private const val NOTIFICATION_ID = 1
    }
}
