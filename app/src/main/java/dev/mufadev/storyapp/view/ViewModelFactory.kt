package dev.mufadev.storyapp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.mufadev.storyapp.preference.UserPreference
import dev.mufadev.storyapp.view.addstory.AddStoryViewModel
import dev.mufadev.storyapp.view.login.LoginViewModel
import dev.mufadev.storyapp.view.main.MainViewModel
import dev.mufadev.storyapp.view.register.RegisterViewModel

class ViewModelFactory(private val pref: UserPreference): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}