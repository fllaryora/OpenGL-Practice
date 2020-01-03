package com.intecanar.openglpractice.opengL.shape

import android.content.res.Resources
import android.opengl.GLES20
import android.util.DisplayMetrics
import com.intecanar.openglpractice.opengL.renderer.OpenGLRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

// basically a circle is a linestring so we need its centre
// radius and how many segments it will consist of
class Circle constructor(  cx : Float,  cy : Float,
                          radius : Float, segments : Int){

    private lateinit var vertexBuffer: FloatBuffer

    private var openGLProgramReference : Int

    init {
        calculatePoints(cx, cy, radius, segments)

        val vertexShader = OpenGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
            vertexShaderCode)
        val fragmentShader = OpenGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
            fragmentShaderCode)

        // create empty OpenGL ES Program
        openGLProgramReference = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }


    }

    // calculate the segments
    fun calculatePoints( cx : Float, cy : Float,
                            radius : Float, segments : Int) {
        val dm : DisplayMetrics =  Resources.getSystem().displayMetrics
        val coordinateSize = segments * COORDS_PER_VERTEX
        val coordinates = FloatArray(coordinateSize)

        for (coordinateIndex in 0..(coordinateSize-COORDS_PER_VERTEX) step COORDS_PER_VERTEX) {

            val percent : Float = (coordinateIndex.toFloat() / (segments.toFloat() - 1f))
            val percentInRadians : Float = 2f * percent * Math.PI.toFloat()

            val aspectRatio = dm.widthPixels.toFloat()/dm.heightPixels.toFloat()

            //Vertex position
            val xi = cx + radius * cos(percentInRadians)
            val yi = cy + radius * sin(percentInRadians) * aspectRatio


            coordinates[coordinateIndex] = xi
            coordinates[coordinateIndex+1] = yi
            coordinates[coordinateIndex+2] = 0f

        }

        // initialise vertex byte buffer for shape coordinates
        val byteBuffer = ByteBuffer.allocateDirect(coordinates.size * BYTES_PER_FLOAT)
        // use the device hardware's native byte order
        byteBuffer.order(ByteOrder.nativeOrder())
        // create a floating point buffer from the ByteBuffer
        vertexBuffer = byteBuffer.asFloatBuffer()
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(coordinates)
        // set the buffer to read the first coordinate
        vertexBuffer.position(0)

    }

    // actuall openGL drawing
    fun draw () {
        val vertexCount = vertexBuffer.remaining() / COORDS_PER_VERTEX
        // Add program to the environment
        GLES20.glUseProgram(openGLProgramReference)
        // get handle to vertex shader's vPosition member
        val mPositionHandle = GLES20.glGetAttribLocation(openGLProgramReference, "vPosition")
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            VERTEX_STRIDE, vertexBuffer)
        // get handle to fragment shader's vColor member
        val mColorHandle = GLES20.glGetUniformLocation(openGLProgramReference, "vColor")
        // Draw the triangle, using triangle fan is the easiest way
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle)
        // Set color of the shape (circle)
        GLES20.glUniform4fv(mColorHandle, 1, floatArrayOf(0.5f, 0.3f, 0.1f, 1f), 0)

    }

    companion object {

        // for rendering the vertices of a shape.
        val vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}"

        //for rendering the face of a shape with colors or textures.
        val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"

        const val COORDS_PER_VERTEX = 3

        const val BYTES_PER_FLOAT = 4

        const val VERTEX_STRIDE: Int = COORDS_PER_VERTEX * BYTES_PER_FLOAT
    }
}