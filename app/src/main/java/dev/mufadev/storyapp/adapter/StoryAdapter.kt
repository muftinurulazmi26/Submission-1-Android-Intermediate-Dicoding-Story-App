package dev.mufadev.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.mufadev.storyapp.databinding.ItemStoryBinding
import dev.mufadev.storyapp.model.Story
import dev.mufadev.storyapp.view.detail.DetailStoryActivity

class StoryAdapter: RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    private val storyData = ArrayList<Story>()

    fun setData(stories: ArrayList<Story>) {
        storyData.clear()
        storyData.addAll(stories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StoryViewHolder(
        ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) =
        holder.bind(storyData[position])

    override fun getItemCount() = storyData.size

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(binding.ivItemPhoto)
                tvItemName.text = story.name
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java).apply {
                    putExtra(DetailStoryActivity.EXTRA_DETAIL, story)
                }
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        androidx.core.util.Pair(binding.ivItemPhoto, "detail_photo"),
                        androidx.core.util.Pair(binding.tvItemName, "detail_name"),
                    )

                it.context.startActivity(intent,optionsCompat.toBundle())
            }
        }
    }
}