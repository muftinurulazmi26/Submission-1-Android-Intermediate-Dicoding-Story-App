package dev.mufadev.storyapp.widget

import android.content.Intent
import android.widget.RemoteViewsService
import dev.mufadev.storyapp.repo.StoryRepository

class StackWidgetService(private val storyRepository: StoryRepository) : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        StackRemoteViewsFactory(this.applicationContext, storyRepository)
}