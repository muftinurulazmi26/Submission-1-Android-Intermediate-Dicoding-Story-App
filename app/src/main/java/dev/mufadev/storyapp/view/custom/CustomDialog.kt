package dev.mufadev.storyapp.view.custom

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import dev.mufadev.storyapp.R

class CustomDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_loading);
    }
}