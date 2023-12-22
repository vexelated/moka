package com.capstoneapps.moka.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstoneapps.moka.data.repository.FailedException
import com.capstoneapps.moka.databinding.ActivityRegisterBinding
import com.capstoneapps.moka.ui.ViewModelFactory
import com.capstoneapps.moka.ui.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[RegisterViewModel::class.java]
        setupAction()
    }
    private fun setupAction() {
        binding.register.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val jsonBody = """
                    {
                        "name": "$name",
                        "email": "$email",
                        "password": "$password"
                    }
                """.trimIndent()

            // Convert the JSON string to RequestBody
            val requestBody: RequestBody =
                jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
            val isNameValid = name.isNotEmpty()
            val isEmailValid = binding.emailEditText.isValid()
            val isPasswordValid = binding.passwordEditText.isValid()

            if (isNameValid && isEmailValid && isPasswordValid) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        viewModel.register(requestBody)
                        runOnUiThread {
                            showSuccessMessage("Akun Sudah di registrasi. Silahkan Login login.")
                        }
                    } catch (e: FailedException) {
                        runOnUiThread {
                            showErrorMessage(e.message ?: "Akun gagal di registrasi")
                        }
                    }
                }
            } else {
                showErrorMessage("Please complete all data correctly")
            }
        }
    }
    private fun showErrorMessage(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun showSuccessMessage(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Success")
            setMessage(message)
            setPositiveButton("Ke Halaman Login") { dialog, _ ->
                dialog.dismiss() // Tutup dialog
                // Memulai LoginActivity
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            create()
            show()
        }
    }
}
