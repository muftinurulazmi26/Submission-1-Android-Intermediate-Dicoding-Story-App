package dev.mufadev.storyapp.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import dev.mufadev.storyapp.R
import dev.mufadev.storyapp.repo.StoryRepository
import dev.mufadev.storyapp.model.Story


internal class StackRemoteViewsFactory(private val mContext: Context, private val storyRepository: StoryRepository) : RemoteViewsService.RemoteViewsFactory {
    private var storyData = ArrayList<Story>()

    override fun onCreate() {
        
    }

    override fun onDataSetChanged() {
        storyData.clear()
        storyData = storyRepository.stories.value?.map { Story(it.id,it.name,it.description,it.photoUrl,it.createdAt,it.lat,it.lon) } as ArrayList<Story>
    }

    override fun onDestroy() {
        
    }

    override fun getCount(): Int = storyData.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        val bitmap = Glide.with(mContext)
            .asBitmap()
            .load(storyData[position].photoUrl)
            .submit(512, 512)
            .get();
        rv.setImageViewBitmap(R.id.imageView, bitmap)
        val extras = bundleOf(
            ListStoryAppWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews ? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}