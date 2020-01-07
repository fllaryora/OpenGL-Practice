package com.intecanar.openglpractice.opengL.renderer.program

import android.opengl.GLES20
import com.intecanar.openglpractice.opengL.renderer.AttribVariable
import com.intecanar.openglpractice.opengL.renderer.Utilities


abstract class Program {

    companion object {
        private const val INVALID_SHADER = 0
    }
    var handle: Int = 0
        private set
    private var vertexShaderHandle: Int =
        INVALID_SHADER
    private var fragmentShaderHandle: Int =
        INVALID_SHADER
    private var mInitialized: Boolean = false

    init {
        mInitialized = false
    }

    @JvmOverloads
    fun init( vertexShaderCode: String, fragmentShaderCode: String, programVariables: Array<AttribVariable> ) {

        vertexShaderHandle = Utilities.loadShader(
            GLES20.GL_VERTEX_SHADER,
            vertexShaderCode
        )
        fragmentShaderHandle = Utilities.loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            fragmentShaderCode
        )

        handle = Utilities.createProgram(
            vertexShaderHandle, fragmentShaderHandle, programVariables
        )

        mInitialized = true
    }

    fun delete() {
        GLES20.glDeleteShader(vertexShaderHandle)
        GLES20.glDeleteShader(fragmentShaderHandle)
        GLES20.glDeleteProgram(handle)
        mInitialized = false
    }

    fun initialized(): Boolean {
        return mInitialized
    }
}