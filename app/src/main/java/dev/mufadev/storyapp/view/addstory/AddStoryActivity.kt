package dev.mufadev.storyapp.view.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import dev.mufadev.storyapp.databinding.ActivityAddStoryBinding
import dev.mufadev.storyapp.preference.UserPreference
import dev.mufadev.storyapp.network.ApiConfig
import dev.mufadev.storyapp.utils.Helper.Companion.createCustomTempFile
import dev.mufadev.storyapp.utils.Helper.Companion.reduceFileImage
import dev.mufadev.storyapp.utils.Helper.Companion.uriToFile
import dev.mufadev.storyapp.view.ViewModelFactory
import dev.mufadev.storyapp.view.custom.CustomDialog
import dev.mufadev.storyapp.view.login.LoginViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {
    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 6
    }
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var loginViewModel: LoginViewModel
    protected var dialog_loader: CustomDialog? = null
    private lateinit var currentPhotoPath: String
    private lateinit var token: String
    private var getFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        if (!allPermissionGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        setupViewModel()
        setupView()
        setupAction()
    }

    private fun setupView() {
        dialog_loader = CustomDialog(this)
    }

    private fun setupAction() {
        binding.btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)

            createCustomTempFile(application).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this@AddStoryActivity,
                    "dev.mufadev.storyapp",
                    it
                )
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launcherIntentCamera.launch(intent)
            }
        }

        binding.btnGallery.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Choose a Picture")
            launcherIntentGallery.launch(chooser)
        }

        binding.buttonAdd.setOnClickListener {
            if (getFile != null) {
                val file = reduceFileImage(getFile as File)

                val description = "${binding.edAddDescription.text}".toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                showLoading()
                ApiConfig.getApiService().addStory(description, imageMultipart, "Bearer ${token}")
                    .enqueue(object : Callback<ResponseBody>{
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            hideLoading()
                            if (response.isSuccessful){
                                val stringResponse = response.body()?.string()
                                val jsonObject = JSONObject(stringResponse)
                                val error = jsonObject.get("error") as Boolean
                                val message = jsonObject.get("message") as String
                                if (!error){
                                    finish()
                                }
                                Toast.makeText(this@AddStoryActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            hideLoading()
                            Toast.makeText(this@AddStoryActivity, t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this@AddStoryActivity, "Input Your Image First!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewModel() {
         addStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddStoryViewModel::class.java]

        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this) { user ->
            if (user.token.isNotEmpty()){
                token = user.token
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (!allPermissionGranted()){
                Toast.makeText(this, "Didn't get permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@AddStoryActivity)

            getFile = myFile

            binding.previewImage.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewImage.setImageBitmap(result)
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