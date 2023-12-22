package com.capstoneapps.moka.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstoneapps.moka.data.di.Injection
import com.capstoneapps.moka.data.repository.UserRepository
import com.capstoneapps.moka.ui.detail.DetailViewmodel
import com.capstoneapps.moka.ui.login.LoginViewModel
import com.capstoneapps.moka.ui.main.MainViewModel
import com.capstoneapps.moka.ui.note.NoteViewModel
import com.capstoneapps.moka.ui.register.RegisterViewModel
import com.capstoneapps.moka.ui.welcome.WelcomeViewmodel

class ViewModelFactory (private val userRepository: UserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(userRepository) as T
        }else if (modelClass.isAssignableFrom(DetailViewmodel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewmodel(userRepository) as T
        }else if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(userRepository) as T
        }else if (modelClass.isAssignableFrom(WelcomeViewmodel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WelcomeViewmodel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            val userRepository = Injection.provideRepository(context)
            if (instance == null) {
                instance = ViewModelFactory(userRepository)
            }
            return instance as ViewModelFactory
        }
    }
}