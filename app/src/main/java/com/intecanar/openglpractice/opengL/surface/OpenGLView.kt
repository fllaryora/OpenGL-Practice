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
    var isRenderSet = false
    constructor(context: Context) : super(context) {
        init ()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init ()
    }

    private fun init (){
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        renderer = OpenGLRenderer(context)
        setRenderer(renderer!!)
        isRenderSet = true
    }

    companion object {
        val dm : DisplayMetrics =  Resources.getSystem().displayMetrics
        var width : Float =  dm.widthPixels.toFloat()
        var height : Float = dm.heightPixels.toFloat()
        var aspectRatio : Float = width / height
    }
}

