package dev.mufadev.storyapp.network

import com.google.gson.annotations.SerializedName
import dev.mufadev.storyapp.model.Story

data class StoryResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("listStory")
    val listStory: List<Story>
)