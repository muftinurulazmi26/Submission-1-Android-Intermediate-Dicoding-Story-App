package dev.mufadev.storyapp.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import dev.mufadev.storyapp.databinding.ActivityDetailStoryBinding
import dev.mufadev.storyapp.model.Story

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    companion object{
        const val EXTRA_DETAIL = "extra_detail"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        val detail = intent.getParcelableExtra<Story>(EXTRA_DETAIL)

        binding.apply {
            tvDetailName.text = detail?.name
            tvDetailDescription.text = detail?.description
        }
        Glide.with(this)
            .load(detail?.photoUrl)
            .into(binding.ivDetailPhoto)
    }
}