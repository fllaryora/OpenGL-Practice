package com.intecanar.openglpractice.opengL.color

import android.graphics.Color as AndroidColor

object ColorTranslator {

    fun mapAndroidColorToOpenGLColor(colorString: String) : Color{
        val parsedColor : Int = AndroidColor.parseColor(colorString)
        val redComponent = AndroidColor.red(parsedColor).toFloat() / 255f
        val greenComponent = AndroidColor.green(parsedColor).toFloat() / 255f
        val blueComponent = AndroidColor.blue(parsedColor).toFloat() / 255f
        val alphaComponent =  AndroidColor.alpha(parsedColor).toFloat() / 255f
        return Color(red = redComponent, green =  greenComponent,
                    blue = blueComponent,alpha = alphaComponent)
    }

}