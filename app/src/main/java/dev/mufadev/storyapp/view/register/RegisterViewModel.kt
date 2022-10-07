package dev.mufadev.storyapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mufadev.storyapp.preference.UserPreference
import dev.mufadev.storyapp.network.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val pref: UserPreference) : ViewModel() {
    private val _loading : MutableLiveData<Boolean> = MutableLiveData()
    private val _error_message : MutableLiveData<String> = MutableLiveData()
    private val _response : MutableLiveData<String> = MutableLiveData()
    val loading: LiveData<Boolean> = _loading
    val error_message: LiveData<String> = _error_message
    val response: LiveData<String> = _response

    fun addUser(name: String, email: String, password: String) = viewModelScope.launch{
        _loading.value = true
        ApiConfig.getApiService().addUser(name, email, password)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    _loading.value = false
                    if (response.isSuccessful){
                        _response.value =  response.body()?.string()
                    } else {
                        val stringResponse = response.errorBody()?.string()
                        val jsonObject = JSONObject(stringResponse)
                        val message = jsonObject.get("message") as String
                        _error_message.value = message
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    _loading.value = false
                    _error_message.value = t.message
                }
            })
    }
}