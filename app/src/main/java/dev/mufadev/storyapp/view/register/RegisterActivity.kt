package dev.mufadev.storyapp.view.register

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
import dev.mufadev.storyapp.databinding.ActivityRegisterBinding
import dev.mufadev.storyapp.preference.UserPreference
import dev.mufadev.storyapp.view.ViewModelFactory
import dev.mufadev.storyapp.view.custom.CustomDialog
import dev.mufadev.storyapp.view.login.LoginActivity
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    protected var dialog_loader: CustomDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -35f,35f).apply{
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val etName = ObjectAnimator.ofFloat(binding.layoutNama, View.ALPHA, 1f).setDuration(500)
        val etEmail = ObjectAnimator.ofFloat(binding.layoutEmail, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.layoutPassword, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(etName, etEmail, etPassword, btnRegister)
            start()
        }
    }

    private fun setupView() {
        dialog_loader = CustomDialog(this)
        registerViewModel.loading.observe(this) {
            if (it) showLoading() else hideLoading()
        }
        registerViewModel.response.observe(this){
            val jsonObject = JSONObject(it)
            val error = jsonObject.get("error") as Boolean
            if (!error){
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        registerViewModel.error_message.observe(this){
            Toast.makeText(this@RegisterActivity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.layoutNama.error = "Masukkan name"
                }
                email.isEmpty() -> {
                    binding.layoutEmail.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.layoutPassword.error = "Masukkan password"
                }
                else -> {
                    registerViewModel.addUser(name, email, password)
                }
            }
        }
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