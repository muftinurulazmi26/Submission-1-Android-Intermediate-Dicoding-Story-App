package dev.mufadev.storyapp.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.mufadev.storyapp.model.Story
import dev.mufadev.storyapp.preference.UserPreference
import dev.mufadev.storyapp.network.ApiConfig
import dev.mufadev.storyapp.network.StoryResponse
import kotlinx.coroutines.flow.first
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository(private val pref: UserPreference) {
    private val _loading : MutableLiveData<Boolean> = MutableLiveData()
    private val _error_message : MutableLiveData<String> = MutableLiveData()
    private val _stories = MutableLiveData<MutableList<Story>>()
    val loading: LiveData<Boolean> = _loading
    val error_message: LiveData<String> = _error_message
    val stories: LiveData<MutableList<Story>> = _stories

    suspend fun getStories() {
        _loading.value = true
        ApiConfig.getApiService().getAllStory("Bearer ${pref.getToken().first()}")
            .enqueue(object : Callback<StoryResponse> {
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    _loading.value = false
                    if (response.isSuccessful){
                        _stories.value = response.body()?.listStory as ArrayList<Story>
                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    _loading.value = false
                    _error_message.value = t.message
                }
            })
    }
}