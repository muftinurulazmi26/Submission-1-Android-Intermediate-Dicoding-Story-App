package dev.mufadev.storyapp.network

import android.service.autofill.UserData
import dev.mufadev.storyapp.model.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    @FormUrlEncoded
    fun addUser(@Field("name") name: String,
                @Field("email")email: String,
                @Field("password")password: String): Call<ResponseBody>

    @POST("login")
    @FormUrlEncoded
    fun getUser(@Field("email")email: String,
                @Field("password")password: String): Call<ResponseBody>

    @Multipart
    @POST("stories")
    fun addStory(@Part("description")description: RequestBody,
                 @Part file: MultipartBody.Part,
                 @Header("Authorization")token: String): Call<ResponseBody>

    @GET("stories")
    fun getAllStory(
        @Header("Authorization")token: String): Call<StoryResponse>
}