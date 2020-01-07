package com.intecanar.openglpractice.opengL.renderer

import android.opengl.GLES20
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
https://github.com/d3alek/Texample2/blob/master/Texample2/src/com/android/texample2/Utilities.java
 */
object Utilities {

    private const val INVALID_SHADER = 0
    private const val BYTES_PER_FLOAT = 4
    private const  val INVALID_PROGRAM = 0
    private const val TAG = "Utilities"

    /**
     * An OpenGL program is simply one vertex shader and one fragment shader
    linked together into a single object. Vertex shaders and fragment shaders
    always go together.
     */
    fun createProgram(vertexShaderHandle: Int, fragmentShaderHandle: Int,
                      attributes: Array<AttribVariable>): Int {
        var mProgram = GLES20.glCreateProgram()

        if (mProgram != INVALID_PROGRAM) {
            GLES20.glAttachShader(mProgram, vertexShaderHandle)
            GLES20.glAttachShader(mProgram, fragmentShaderHandle)

            for (attribute in attributes) {
                GLES20.glBindAttribLocation(mProgram, attribute.handle, attribute.attributeName)
            }

            GLES20.glLinkProgram(mProgram)

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0)

            if (linkStatus[0] == GLES20.GL_FALSE) {
                Log.e(TAG, GLES20.glGetProgramInfoLog(mProgram))
                GLES20.glDeleteProgram(mProgram)
                mProgram = INVALID_PROGRAM
            }
        }

        if (mProgram == INVALID_PROGRAM) {
            throw RuntimeException("Error creating program.")
        }
        return mProgram
    }

    fun loadShader(type: Int, shaderCode: String): Int {
        var shaderHandle = GLES20.glCreateShader(type)

        if (shaderHandle != INVALID_SHADER) {
            GLES20.glShaderSource(shaderHandle, shaderCode)
            GLES20.glCompileShader(shaderHandle)

            // Get the compilation status.
            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == GLES20.GL_FALSE) {
                Log.e(TAG, "Shader fail info: " + GLES20.glGetShaderInfoLog(shaderHandle))
                GLES20.glDeleteShader(shaderHandle)
                shaderHandle = INVALID_SHADER
            }
        }

        if (shaderHandle == INVALID_SHADER) {
            throw RuntimeException("Error creating shader $type")
        }
        return shaderHandle
    }

    fun newFloatBuffer(verticesData: FloatArray): FloatBuffer {
        val floatBuffer: FloatBuffer = ByteBuffer.allocateDirect(verticesData.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        floatBuffer.put(verticesData).position(0)
        return floatBuffer
    }
}