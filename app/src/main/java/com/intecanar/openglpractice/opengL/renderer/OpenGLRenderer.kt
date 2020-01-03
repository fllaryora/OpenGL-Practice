package com.intecanar.openglpractice.opengL.renderer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.intecanar.openglpractice.opengL.shape.Circle
import com.intecanar.openglpractice.opengL.surface.OpenGLView
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer() : GLSurfaceView.Renderer {

    private lateinit var  circle: Circle

    private  var objectsReady = false

   // fun getCircle():Circle = circle


    override fun onDrawFrame(gl: GL10?) {
        // Redraw background color
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        circle.draw()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        OpenGLView.aspectRatio = width.toFloat()/ height.toFloat()
    }

    override fun onSurfaceCreated(gl: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        circle = Circle(0f,0f, 0.2f, 75)
        objectsReady = true
    }

    companion object {

        fun loadShader(type: Int, shaderCode: String): Int {

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            return GLES20.glCreateShader(type).also { shader ->

                // add the source code to the shader and compile it
                GLES20.glShaderSource(shader, shaderCode)
                GLES20.glCompileShader(shader)
            }
        }
    }


}