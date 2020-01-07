package com.intecanar.openglpractice

import android.app.ActivityManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.intecanar.openglpractice.opengL.surface.OpenGLView


class MainActivity : AppCompatActivity() {

    private lateinit var gLView: OpenGLView
    private var isGlViewSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //quit status bar
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val supportES2 = configurationInfo.reqGlEsVersion >= 0x00020000
        if(supportES2){
            gLView = OpenGLView(this)
            setContentView(gLView)
            isGlViewSet = true
        }
         else{
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if(isGlViewSet && this.gLView.isRenderSet) {
            this.gLView.onResume()
        }

    }

    override fun onPause() {
        super.onPause()
        if(isGlViewSet && this.gLView.isRenderSet) {
            this.gLView.onPause()
        }
    }
}
