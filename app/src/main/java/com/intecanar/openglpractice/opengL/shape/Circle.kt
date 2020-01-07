package com.intecanar.openglpractice.opengL.shape


import android.opengl.GLES20
import com.intecanar.openglpractice.opengL.renderer.AttribVariable
import com.intecanar.openglpractice.opengL.renderer.Uniform
import com.intecanar.openglpractice.opengL.renderer.Utilities
import com.intecanar.openglpractice.opengL.renderer.program.CircleProgram
import com.intecanar.openglpractice.opengL.surface.OpenGLView
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

// basically a circle is a linestring so we need its centre
// radius and how many segments it will consist of
class Circle constructor(private val  centerPoint: Pair<Float,Float>,
                         private val radius : Float,
                         private val segments : Int,
                         private val color : FloatArray){

    private lateinit var vertexBuffer: FloatBuffer

    private var openGLProgramReference : CircleProgram

    init {
        drawCircleInGLBuffer()
        // create empty OpenGL ES Program
        this.openGLProgramReference = CircleProgram()
        this.openGLProgramReference.init()
    }

    fun drawCircleInGLBuffer( ) {
        val coordinateSize = this.segments * COORDS_PER_VERTEX
        val coordinates = FloatArray(coordinateSize)

        for (coordinateIndex in 0..(coordinateSize-COORDS_PER_VERTEX) step COORDS_PER_VERTEX) {

            val percent : Float = (coordinateIndex.toFloat() / (this.segments.toFloat() - 1f))
            val percentInRadians : Float = 2f * percent * Math.PI.toFloat()

            //val aspectRatio =
            val aspectRatio = OpenGLView.aspectRatio
            //Vertex position
            val xi = centerPoint.first + this.radius * cos(percentInRadians)
            val yi = centerPoint.second + this.radius * sin(percentInRadians) * aspectRatio

            coordinates[coordinateIndex] = xi
            coordinates[coordinateIndex+1] = yi
            coordinates[coordinateIndex+2] = 0f
        }

        this.vertexBuffer =  Utilities.newFloatBuffer(coordinates)
    }

    /**
     * draw circle using its program
     * it is called when  this.gLView.requestRender() is called
     */
    fun draw () {

        this.vertexBuffer.position(0)

        val vertexCount = this.vertexBuffer.remaining() / COORDS_PER_VERTEX
        // Add program to the environment
        GLES20.glUseProgram(this.openGLProgramReference.handle)

        val mPositionHandle = AttribVariable.V_Position.handle
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            VERTEX_STRIDE, this.vertexBuffer)

        // get handle to fragment shader's vColor member
        val mColorHandle = GLES20.glGetUniformLocation(this.openGLProgramReference.handle,
            Uniform.V_COLOR.uniformName)
        // Draw the triangle, using triangle fan is the easiest way
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle)

        // Set color of the shape (circle)
        GLES20.glUniform4fv(mColorHandle, 1, this.color, 0)

    }

    companion object {

        const val COORDS_PER_VERTEX = 3
        const val BYTES_PER_FLOAT = 4
        const val VERTEX_STRIDE: Int = COORDS_PER_VERTEX * BYTES_PER_FLOAT
    }
}