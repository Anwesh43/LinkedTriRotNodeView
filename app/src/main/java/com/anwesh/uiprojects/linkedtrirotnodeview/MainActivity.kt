package com.anwesh.uiprojects.linkedtrirotnodeview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.trnview.TRNView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : TRNView = TRNView.create(this)
        fullScreen()
        view.addOnAnimationListener({
            showToast("animation number ${it + 1} is complete")
        }, {
            showToast("animation numnber ${it + 1} is reset")
        })
    }

    private fun showToast(text : String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}