package dev.mufadev.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.mufadev.storyapp.repo.StoryRepository
import dev.mufadev.storyapp.preference.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference) : ViewModel() {
    val storyRepository: StoryRepository = StoryRepository(pref)

    fun getStories() = viewModelScope.launch{
        storyRepository.getStories()
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }
}