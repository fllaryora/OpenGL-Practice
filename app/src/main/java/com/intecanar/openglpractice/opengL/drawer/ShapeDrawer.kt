package com.intecanar.openglpractice.opengL.drawer

import android.content.Context
import android.opengl.GLES20
import com.intecanar.openglpractice.opengL.renderer.AttribVariable
import com.intecanar.openglpractice.opengL.renderer.Uniform
import com.intecanar.openglpractice.opengL.renderer.Utilities
import com.intecanar.openglpractice.opengL.renderer.program.ShapesProgram
import com.intecanar.openglpractice.opengL.shapes.Shape
import java.nio.FloatBuffer


class ShapeDrawer constructor( shapeList: List<Shape>, private val context: Context){

    private lateinit var vertexBuffer: FloatBuffer

    private var openGLProgramReference : ShapesProgram
    private val shapes = mutableListOf<Shape>()

    init {
        shapes.addAll(shapeList)
        drawCircleInGLBuffer()
        // create empty OpenGL ES Program
        this.openGLProgramReference = ShapesProgram(context)
    }

    /**
     * Draw 1 circle using triangles
     */
    fun drawCircleInGLBuffer( ) {

        shapes.forEachIndexed { index, shape ->
            val coordinates = shape.getCoordinates()
            //TODO Fix allow multiple shapes
            this.vertexBuffer = Utilities.newFloatBuffer(coordinates)
        }

    }

    /**
     * draw circle using its program
     * it is called when  this.gLView.requestRender() is called
     */
    fun draw () {

        this.vertexBuffer.position(0)
        GLES20.glUseProgram(this.openGLProgramReference.handle)
        val mPositionHandle = AttribVariable.V_Position.handle

        // asosiate mPositionHandle with this.vertexBuffer
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            VERTEX_STRIDE, this.vertexBuffer)
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        val first = 0
        for (shape in shapes) {
            // draw a sucesion of triangles
            val mColorHandle = GLES20.glGetUniformLocation(this.openGLProgramReference.handle,
                Uniform.V_COLOR.uniformName)
            GLES20.glUniform4fv(mColorHandle, 1, shape.color.getArray(), 0)
            val vertexCount = this.vertexBuffer.remaining() / COORDS_PER_VERTEX
            //TODO change fisrt to allow multiple shapes
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, first, vertexCount)
        }

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle)

    }

    companion object {
        const val BYTES_PER_FLOAT = 4
        const val COORDS_PER_VERTEX = 3
        const val VERTEX_STRIDE: Int = COORDS_PER_VERTEX * BYTES_PER_FLOAT
    }
}