package com.polusov.infinitediagram.tests.keyboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.polusov.infinitediagram.hideKeyboard
import com.polusov.infinitediagram.R
import com.polusov.infinitediagram.showKeyboard
import kotlinx.android.synthetic.main.activity_keyboard.*
import timber.log.Timber

class KeyboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyboard)

        editTextSimple.setOnClickListener {
            Timber.d("Clicked")
            editTextSimple.isFocusableInTouchMode = true
            editTextSimple.showKeyboard()
        }

        backButton.setOnClickListener {
            editTextSimple.isFocusableInTouchMode = false
            editTextSimple.hideKeyboard()
        }
    }
}