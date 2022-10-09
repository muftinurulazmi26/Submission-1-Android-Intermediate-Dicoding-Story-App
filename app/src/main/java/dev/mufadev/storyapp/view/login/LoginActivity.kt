package dev.mufadev.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import dev.mufadev.storyapp.databinding.ActivityLoginBinding
import dev.mufadev.storyapp.model.UserModel
import dev.mufadev.storyapp.preference.UserPreference
import dev.mufadev.storyapp.view.ViewModelFactory
import dev.mufadev.storyapp.view.custom.CustomDialog
import dev.mufadev.storyapp.view.main.MainActivity
import dev.mufadev.storyapp.view.register.RegisterActivity
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    protected var dialog_loader: CustomDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -35f,35f).apply{
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val etEmail = ObjectAnimator.ofFloat(binding.layoutEmail, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.layoutPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(etEmail, etPassword, btnLogin)
            start()
        }
    }

    private fun setupView() {
        dialog_loader = CustomDialog(this)

        loginViewModel.getUser().observe(this) { user ->
            if (user.isLogin){
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        loginViewModel.loading.observe(this) {
            if (it) showLoading() else hideLoading()
        }
        loginViewModel.response.observe(this){
            val jsonObject = JSONObject(it)
            val error = jsonObject.get("error") as Boolean
            if (!error){
                val loginResult = jsonObject.getJSONObject("loginResult")
                val userId = loginResult.get("userId") as String
                val name = loginResult.get("name") as String
                val token = loginResult.get("token") as String
                loginViewModel.saveUser(UserModel(userId, name, token))
                loginViewModel.login()
            }
        }
        loginViewModel.error_message.observe(this){
            Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.layoutEmail.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.layoutPassword.error = "Masukkan password"
                }
                else -> {
                    loginViewModel.doLogin(email, password)
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupViewModel() {
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