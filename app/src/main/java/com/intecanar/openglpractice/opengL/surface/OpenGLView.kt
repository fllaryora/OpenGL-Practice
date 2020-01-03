package com.intecanar.openglpractice.opengL.surface

import android.content.res.Resources
import android.util.DisplayMetrics
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import com.intecanar.openglpractice.opengL.renderer.OpenGLRenderer

class OpenGLView: GLSurfaceView {
    var renderer : OpenGLRenderer? = null

    constructor(context: Context) : super(context) {
        init ()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init ()
    }

    fun init (){
        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        renderer = OpenGLRenderer()
        setRenderer(renderer!!)
    }

    companion object {
        val dm : DisplayMetrics =  Resources.getSystem().displayMetrics
        var width : Float =  dm.widthPixels.toFloat()
        var height : Float = dm.heightPixels.toFloat()
        var aspectRatio : Float = width / height
    }
}

