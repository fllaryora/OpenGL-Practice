package com.intecanar.openglpractice.opengL.surface

import android.content.res.Resources
import android.util.DisplayMetrics
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import com.intecanar.openglpractice.opengL.renderer.OpenGLRenderer
import com.intecanar.openglpractice.opengL.text.Texample2Renderer

class OpenGLView: GLSurfaceView {
    //var renderer : OpenGLRenderer? = null
    var rendererText : Texample2Renderer? = null
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
        //renderer = OpenGLRenderer(context)
        rendererText = Texample2Renderer(context)
        //setRenderer(renderer!!)
        setRenderer(rendererText!!)
        isRenderSet = true
    }

    companion object {
        val dm : DisplayMetrics =  Resources.getSystem().displayMetrics
        var width : Int =  dm.widthPixels
        var height : Int = dm.heightPixels
        var aspectRatio : Float = width.toFloat() / height.toFloat()
    }
}

