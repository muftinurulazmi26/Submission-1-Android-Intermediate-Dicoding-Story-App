package dev.mufadev.storyapp.view.login

import androidx.lifecycle.*
import dev.mufadev.storyapp.model.UserModel
import dev.mufadev.storyapp.preference.UserPreference
import dev.mufadev.storyapp.network.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference): ViewModel() {
    private val _loading : MutableLiveData<Boolean> = MutableLiveData()
    private val _error_message : MutableLiveData<String> = MutableLiveData()
    private val _response : MutableLiveData<String> = MutableLiveData()
    val loading: LiveData<Boolean> = _loading
    val error_message: LiveData<String> = _error_message
    val response: LiveData<String> = _response

    fun doLogin (email: String, password: String) = viewModelScope.launch{
        _loading.value = true
        ApiConfig.getApiService().getUser(email, password)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    _loading.value = false
                    if (response.isSuccessful){
                        val stringResponse = response.body()?.string()
                        val jsonObject = JSONObject(stringResponse)
                        val error = jsonObject.get("error") as Boolean
                        val message = jsonObject.get("message") as String
                        if (!error){
                            _response.value = stringResponse
                        } else {
                            _error_message.value = message
                        }
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

    fun saveUser(user: UserModel){
        viewModelScope.launch {
            pref.saveuser(user)
        }
    }

    fun getUser(): LiveData<UserModel>{
        return pref.getUser().asLiveData()
    }

    fun login() {
        viewModelScope.launch {
            pref.login()
        }
    }

    fun logout(){
        viewModelScope.launch {
            pref.logout()
        }
    }
}