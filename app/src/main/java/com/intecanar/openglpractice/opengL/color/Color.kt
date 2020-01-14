package com.intecanar.openglpractice.opengL.color

data class Color (val red: Float, val green: Float, val blue: Float, val alpha: Float) {
    fun getArray (): FloatArray {
        return floatArrayOf(red, green, blue, alpha)
    }
}