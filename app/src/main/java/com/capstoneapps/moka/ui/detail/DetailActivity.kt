package com.capstoneapps.moka.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstoneapps.moka.databinding.ActivityDetailBinding
import com.capstoneapps.moka.ui.ViewModelFactory
import com.capstoneapps.moka.ui.main.MainActivity
import com.capstoneapps.moka.ui.note.NoteActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel using ViewModelProvider and ViewModelFactory
        detailViewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(this))[DetailViewmodel::class.java]

        // Access views from the layout using binding
        val titleEditText = binding.detailEdTitle
        val descriptionEditText = binding.detailEdDescription

        // Access the "Done" button
        val doneButton = binding.btnDoneTask

        // Set OnClickListener for the "Done" button
        doneButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val jsonBody = """
                    {
                        "taskName": "$title",
                        "taskDescription": "$description",
                        "taskDate": "",
                        "statusTask": false
                    }
                """.trimIndent()

            val requestBody: RequestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = detailViewModel.creaTask(requestBody)

                    // Check if the response status is successful (assuming 2xx status codes)
                    if (response.status == 201) {
                        // Task creation success
                        val taskId = response.data?.taskId
                        runOnUiThread {
                            showToast("Task created successfully! Task ID: $taskId")
                        }
                    } else {
                        // Task creation failed
                        runOnUiThread {
                            showToast("Failed to create task. Message: ${response.message}")
                        }
                    }
                } catch (e: Exception) {
                    // Handle network or other exceptions
                    runOnUiThread {
                        showToast("Failed to create task. Exception: ${e.message}")
                    }
                }
            }
            navigateToNoteActivity()
        }
    binding.btnBack.setOnClickListener{
        navigateToMainActivity()
    }
    }
    override fun onDestroy() {
        super.onDestroy()

        // Setelah aktivitas dihancurkan, kirim data kembali ke TaskFragment jika diperlukan
        val resultIntent = Intent()
        // resultIntent.putExtra("key", value)
        setResult(Activity.RESULT_OK, resultIntent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
    }
    private fun navigateToNoteActivity() {
        val intent = Intent(this, NoteActivity::class.java)
        startActivity(intent)
        finish() // Ini akan menghapus activity saat ini dari tumpukan aktivitas
    }
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Ini akan menghapus activity saat ini dari tumpukan aktivitas
    }


}

