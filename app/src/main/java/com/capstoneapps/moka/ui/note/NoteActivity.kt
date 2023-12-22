package com.capstoneapps.moka.ui.note

import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstoneapps.moka.R
import com.capstoneapps.moka.databinding.ActivityNoteBinding
import com.capstoneapps.moka.ui.ViewModelFactory
import com.capstoneapps.moka.ui.main.MainActivity
import com.capstoneapps.moka.ui.note.adapterTask.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

@Suppress("DEPRECATION")
class NoteActivity : AppCompatActivity() {

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.Task,
            R.string.History_Task
        )
    }

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize NoteViewModel using ViewModelFactory
        noteViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[NoteViewModel::class.java]

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f
        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    // Navigasi ke NoteActivity
                    navigateToNoteMainActivty()
                    true
                }
                // Tambahkan case untuk setiap item menu lainnya sesuai kebutuhan
                else -> false
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Handle the back press here
        // You can start the main activity or perform any other desired action
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun navigateToNoteMainActivty() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}
