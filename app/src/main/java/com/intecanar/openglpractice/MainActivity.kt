package com.intecanar.openglpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.intecanar.openglpractice.opengL.surface.OpenGLView

class MainActivity : AppCompatActivity() {

    private lateinit var gLView: OpenGLView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //quit status bar
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

        gLView = OpenGLView(this)
        setContentView(gLView)

    }


    override fun onPointerCaptureChanged(hasCapture: Boolean) {
        //super.onPointerCaptureChanged(hasCapture)
    }

    override fun onResume() {
        super.onResume()
        this.gLView.onResume()
    }

    override fun onPause() {
        super.onPause()
        this.gLView.onPause()
    }

}
