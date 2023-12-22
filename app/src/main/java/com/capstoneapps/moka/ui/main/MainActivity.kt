package com.capstoneapps.moka.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.capstoneapps.moka.R
import com.capstoneapps.moka.databinding.ActivityMainBinding
import com.capstoneapps.moka.ui.ViewModelFactory
import com.capstoneapps.moka.ui.camera.DrowsinessActivity
import com.capstoneapps.moka.ui.login.LoginActivity
import com.capstoneapps.moka.ui.note.NoteActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION", "NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private var isCountingDown = false
    private var isPaused = false
    private lateinit var countdownReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.start.setText("start")
        mainViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[MainViewModel::class.java]
        // Observe the countdownTime LiveData
        mainViewModel.countdownTime.observe(this) { timeInMillis ->
            tampilkanTimerFokus(timeInMillis)
        }
        mainViewModel.countdownTimeIstirahat.observe(this) { timeInMillis ->
            tampilkanTimerIstirahat(timeInMillis)
        }
        aturIndikasiFokus()
        setupBroadcastReceiver()
        binding.start.setOnClickListener {
            if (!isCountingDown) {
                mulaiTimerFokus()
                binding.start.setText("pasue")
                aturIndikasiFokus()
            } else {
                // Timer sedang berjalan, maka pause atau resume sesuai kondisi
                if (isPaused) {
                    // Jika timer sedang di-pause, resume timer
                    resumeTimer()
                } else {
                    // Jika timer sedang berjalan, pause timer
                    pauseTimer()
                }
            }
        }
        binding.pengenalanWajah.setOnClickListener {
            navigateToDrowsinessActivity()
        }


        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_note -> {
                    // Navigasi ke NoteActivity
                    navigateToNoteActivity()
                    true
                }
                // Tambahkan case untuk setiap item menu lainnya sesuai kebutuhan
                else -> false
            }
        }
        binding.focusIndication.setOnClickListener {
            aturIndikasiFokus()
        }
        binding.breakIndication.setOnClickListener {
            aturIndikasiIstirahat()
        }
    }
    private fun aturIndikasiFokus() {
        binding.istirahat.visibility = View.INVISIBLE
        binding.focus.visibility = View.VISIBLE
        binding.focusIndication.setBackgroundColor(Color.RED)
        binding.breakIndication.setBackgroundColor(Color.WHITE)
    }

    private fun aturIndikasiIstirahat() {
        binding.focus.visibility = View.INVISIBLE
        binding.istirahat.visibility = View.VISIBLE
        binding.breakIndication.setBackgroundColor(Color.RED)
        binding.focusIndication.setBackgroundColor(Color.WHITE)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_user, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Logout -> {
                // Panggil fungsi logout dari ViewModel
                CoroutineScope(Dispatchers.Main).launch {
                    mainViewModel.logOut()
                    navigateToLoginActivity()
                }
                true
            }
            // Tambahan tanggapan untuk item menu lainnya sesuai kebutuhan
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBroadcastReceiver() {
        countdownReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    CountdownService.COUNTDOWN_UPDATE -> {
                        val millisUntilFinished = intent.getLongExtra(CountdownService.COUNTDOWN_TIME, 0)
                        mainViewModel.setCountdownTime(millisUntilFinished)
                        aturIndikasiFokus()                    }
                    CountdownService.COUNTDOWN_UPDATE_BREAK -> {
                        val millisUntilFinished = intent.getLongExtra(CountdownService.COUNTDOWN_TIME_BREAK, 0)
                        mainViewModel.setCountdownTimeIstirahat(millisUntilFinished)
                        aturIndikasiIstirahat()
                    }
                    CountdownService.COUNTDOWN_FINISH -> {
                        handleCountdownFinish()
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            countdownReceiver,
            IntentFilter().apply {
                addAction(CountdownService.COUNTDOWN_UPDATE)
                addAction(CountdownService.COUNTDOWN_UPDATE_BREAK)
                addAction(CountdownService.COUNTDOWN_FINISH)
                addAction(CountdownService.COUNTDOWN_START_BREAK)
            }
        )
    }

    private fun mulaiTimerFokus() {
        val minutesText = binding.menitFokus.text?.toString() ?: "0"
        val secondsText = binding.detikFokus.text?.toString() ?: "0"
        val minutesTextBreak = binding.menitIstirahat.text?.toString() ?: ""
        val secondsTextBreak = binding.detikIstirahat.text?.toString() ?: ""
        if (minutesText.isNotEmpty() && minutesTextBreak.isNotEmpty() || secondsText.isNotEmpty() && secondsTextBreak.isNotEmpty()) {
            try {
                val minutes = minutesText.toLongOrNull() ?: 0
                val seconds = secondsText.toLongOrNull() ?: 0
                val totalTimeInMillis = (minutes * 60 + seconds) * 1000

                val minutesTextBreak = minutesTextBreak.toLongOrNull() ?: 0
                val secondsTextBreak = secondsTextBreak.toLongOrNull() ?: 0
                val totalTimeInMillisBreak = (minutesTextBreak * 60 + secondsTextBreak) * 1000

                // Membuat intent untuk memulai Layanan
                val intent = Intent(this, CountdownService::class.java).apply {
                    putExtra(CountdownService.TOTAL_TIME, totalTimeInMillis)
                    putExtra(CountdownService.TOTAL_TIMEBREAK, totalTimeInMillisBreak)
                }

                // Memulai Layanan
                startService(intent)

                // Mengubah status dan tampilan input
                bisaDiUbah(false)
                isCountingDown = true
            } catch (e: NumberFormatException) {
                showToast("Invalid time format")
            }
        } else {
            binding.start.setText("start")
            showToast("Masukkan waktu yang valid")
        }
    }
    private fun handleCountdownFinish() {
        showToast("Selesai!")
        bisaDiUbah(true)
        isCountingDown = false
        // Reset menit dan detik ke nilai default
        binding.menitFokus.setText("")
        binding.detikFokus.setText("")
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun bisaDiUbah(editable: Boolean) {
        binding.menitFokus.setCustomEditable(editable)
        binding.detikFokus.setCustomEditable(editable)
        binding.menitIstirahat.setCustomEditable(editable)
        binding.detikIstirahat.setCustomEditable(editable)

        // Set DuaDigit to be non-editable and show the countdown
        binding.menitFokus.isFocusable = editable
        binding.detikFokus.isFocusable = editable
        binding.menitFokus.isFocusableInTouchMode = editable
        binding.detikFokus.isFocusableInTouchMode = editable
        binding.menitIstirahat.isFocusable = editable
        binding.detikIstirahat.isFocusable = editable
        binding.menitIstirahat.isFocusableInTouchMode = editable
        binding.detikIstirahat.isFocusableInTouchMode = editable
    }
    // Di dalam MainActivity, kamu dapat memanggil fungsi pause dan resume seperti berikut:

    private fun pauseTimer() {
        binding.start.setText("resume")
        isPaused = true
        val intent = Intent(this, CountdownService::class.java)
        intent.action = CountdownService.ACTION_PAUSE_TIMER
        startService(intent)
    }

    private fun resumeTimer() {
        binding.start.setText("start")
        isPaused = false
        val intent = Intent(this, CountdownService::class.java)
        intent.action = CountdownService.ACTION_RESUME_TIMER
        startService(intent)
    }


    private fun tampilkanTimerFokus(millisUntilFinished: Long) {
        val remainingMinutes = millisUntilFinished / 60000
        val remainingSeconds = (millisUntilFinished % 60000) / 1000
        binding.menitFokus.setText(String.format("%02d", remainingMinutes))
        binding.detikFokus.setText(String.format("%02d", remainingSeconds))
    }
    private fun tampilkanTimerIstirahat(millisUntilFinished: Long) {
        val remainingMinutes = millisUntilFinished / 60000
        val remainingSeconds = (millisUntilFinished % 60000) / 1000
        binding.menitIstirahat.setText(String.format("%02d", remainingMinutes))
        binding.detikIstirahat.setText(String.format("%02d", remainingSeconds))
    }
    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(countdownReceiver)
        super.onDestroy()
    }
    private fun navigateToNoteActivity() {
        val intent = Intent(this, NoteActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDrowsinessActivity() {
        val intent = Intent(this, DrowsinessActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Ini akan menghapus activity saat ini dari tumpukan aktivitas
    }

}
