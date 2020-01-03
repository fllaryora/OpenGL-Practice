package com.intecanar.openglpractice.opengL.surface

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.intecanar.openglpractice.opengL.renderer.OpenGLRenderer

class OpenGLView: GLSurfaceView {

    constructor(context: Context) : super(context) {
        init ()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init ()
    }

    fun init (){
        setEGLContextClientVersion(2) // I have used 2 because running on emulator
        preserveEGLContextOnPause = true
        setRenderer(OpenGLRenderer())
    }
}

