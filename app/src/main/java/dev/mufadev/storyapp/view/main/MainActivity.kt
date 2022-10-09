package dev.mufadev.storyapp.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import dev.mufadev.storyapp.adapter.StoryAdapter
import dev.mufadev.storyapp.databinding.ActivityMainBinding
import dev.mufadev.storyapp.model.Story
import dev.mufadev.storyapp.preference.UserPreference
import dev.mufadev.storyapp.view.addstory.AddStoryActivity
import dev.mufadev.storyapp.view.ViewModelFactory
import dev.mufadev.storyapp.view.custom.CustomDialog
import dev.mufadev.storyapp.view.login.LoginActivity
import dev.mufadev.storyapp.view.login.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var storyAdapter: StoryAdapter
    private var stories: ArrayList<Story> = ArrayList()
    protected var dialog_loader: CustomDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.actionAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.actionLogout.setOnClickListener {
            loginViewModel.logout()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        binding.actionSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun setupView() {
        storyAdapter = StoryAdapter()
        dialog_loader = CustomDialog(this)

        mainViewModel.getToken().observe(this){
            CoroutineScope(Dispatchers.IO).launch {
                mainViewModel.getStories()
            }

            mainViewModel.storyRepository.loading.observe(this) {
                if (it) showLoading() else hideLoading()
            }
            mainViewModel.storyRepository.stories.observe(this) {
                storyAdapter.setData(it as ArrayList<Story>)
                if (it.isEmpty()) Toast.makeText(this@MainActivity, "No Data", Toast.LENGTH_SHORT).show()
            }
            mainViewModel.storyRepository.error_message.observe(this){
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            }
        }

        with(binding.rvStory){
            setHasFixedSize(true)
            adapter = storyAdapter
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    private fun showLoading(){
        if (!dialog_loader?.isShowing!!){
            dialog_loader?.show()
        }
    }

    private fun hideLoading(){
        if (dialog_loader?.isShowing!!){
            dialog_loader?.dismiss()
        }
    }
}