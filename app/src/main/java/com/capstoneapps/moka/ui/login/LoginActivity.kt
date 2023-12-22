package com.capstoneapps.moka.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstoneapps.moka.R
import com.capstoneapps.moka.data.api.ApiConfig
import com.capstoneapps.moka.data.pref.UserModel
import com.capstoneapps.moka.data.repository.FailedException
import com.capstoneapps.moka.databinding.ActivityLoginBinding
import com.capstoneapps.moka.ui.ViewModelFactory
import com.capstoneapps.moka.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[LoginViewModel::class.java]

        setupViews()
    }

    private fun setupViews() {
        binding.apply {
            loginButton.setOnClickListener { handleLoginButtonClick() }
        }
    }


    private fun handleLoginButtonClick() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val jsonBody = """
            {
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()
        val requestBody: RequestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
        val isEmailValid = binding.emailEditText.isValid()
        val isPasswordValid = binding.passwordEditText.isValid()

        if (isEmailValid && isPasswordValid) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val loginResponse = loginViewModel.login(requestBody)
                    val accessToken = loginResponse.accessToken

                    val userModel = UserModel(email, accessToken ?: "", isLogin = true)
                    loginViewModel.saveSession(userModel)

                    withContext(Dispatchers.Main) {
                        accessToken?.let { ApiConfig.setAuthToken(it) }
                        navigateToMainActivity()
                    }
                } catch (e: FailedException) {
                    withContext(Dispatchers.Main) {
                        showErrorMessage(e.message ?: getString(R.string.registration_failed_message))
                    }
                }
            }
        } else {
            showErrorMessage(getString(R.string.incomplete_data_message))
        }
    }

    private fun showErrorMessage(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.error_title))
            setMessage(message)
            setPositiveButton(getString(R.string.ok_button), null)
            create()
            show()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
