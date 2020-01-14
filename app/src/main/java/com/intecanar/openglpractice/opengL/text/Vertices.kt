package com.intecanar.openglpractice.opengL.text

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer

import android.opengl.GLES20
import com.intecanar.openglpractice.opengL.renderer.AttribVariable

/**
 * create the vertices/indices as specified (for 2d/3d)
 * @param  maxVertices - maximum vertices allowed in buffer
 * @param maxIndices - maximum indices allowed in buffer
 */

class Vertices (private val maxVertices: Int, private val  maxIndices: Int,
                private var components: ComponentsInVertexPosition) {

    //(Element Size of a Single Vertex)
    private val vertexStride: Int
        get() = this.components.number + COMPONENTS_IN_VERTEX_TEXURE_COORD + COMPONENTS_IN_MVP_MATRIX_INDEX

    // Bytesize of a Single Vertex
    private val vertexSize: Int
        get() = this.vertexStride * 4


    private val vertices = ByteBuffer.allocateDirect(maxVertices * vertexSize)
                            .order(ByteOrder.nativeOrder()).asIntBuffer()

    private fun getShortBuffer() : ShortBuffer? {
        return if (maxIndices > 0){
            ByteBuffer.allocateDirect(maxIndices * INDEX_SIZE)
                .order(ByteOrder.nativeOrder()).asShortBuffer()
        } else {
            null
        }
    }
    private val indices: ShortBuffer? = getShortBuffer()

    private val tmpBuffer = IntArray(maxVertices * vertexSize / 4)
    private val mTextureCoordinateHandle = AttribVariable.A_TexCoordinate.handle
    private val mPositionHandle = AttribVariable.A_Position.handle
    private val mMVPIndexHandle = AttribVariable.A_MVPMatrixIndex.handle

    private var numVertices: Int = 0
    private var numIndices: Int = 0

    /**
     * @param vertices - array of vertices (floats) to set
     * @param offset   - offset to first vertex in array
     * @param length   - number of floats in the vertex array (total)
     *                  for easy setting use: vtx_cnt * (this.vertexSize / 4)
     */
    fun setVertices(vertices: FloatArray, offset: Int, length: Int) {
        // Remove Existing Vertices
        this.vertices.clear()
        // Calculate Last Element
        val last = offset + length
        var i = offset
        var j = 0
        while (i < last) {
            // FOR Each Specified Vertex
            tmpBuffer[j] = java.lang.Float.floatToRawIntBits(vertices[i])
            i++
            j++
        }  // Set Vertex as Raw Integer Bits in Buffer
        // Set New Vertices
        this.vertices.put(tmpBuffer, 0, length)
        // Flip Vertex Buffer
        this.vertices.flip()
        // Save Number of Vertices
        this.numVertices = length / this.vertexStride
    }

    /**
     * @param indices - array of indices (shorts) to set
     * @param offset - offset to first index in array
     * @param length - number of indices in array (from offset)
     */
    fun setIndexBufferForRendering(indices: ShortArray, offset: Int, length: Int) {
        // Clear Existing Indices
        this.indices!!.clear()
        // Set New Indices
        this.indices.put(indices, offset, length)
        // Flip Index Buffer
        this.indices.flip()
        this.numIndices = length
    }

    /**
     * perform all required binding/state changes before rendering batches.
     *    USAGE: call once before calling draw() multiple times for this buffer.
     */
    fun bind() {

        vertices.position(0)
        GLES20.glVertexAttribPointer( mPositionHandle, components.number,
            GLES20.GL_FLOAT, false, vertexSize, vertices )
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        // bind texture position pointer
        vertices.position(components.number)  // Set Vertex Buffer to Texture Coords (NOTE: position based on whether color is also specified)
        GLES20.glVertexAttribPointer(
            mTextureCoordinateHandle, COMPONENTS_IN_VERTEX_TEXURE_COORD,
            GLES20.GL_FLOAT, false, vertexSize, vertices)
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)

        // bind MVP Matrix index position handle
        vertices.position(components.number + COMPONENTS_IN_VERTEX_TEXURE_COORD)
        GLES20.glVertexAttribPointer(mMVPIndexHandle, COMPONENTS_IN_MVP_MATRIX_INDEX,
            GLES20.GL_FLOAT, false, vertexSize, vertices )
        GLES20.glEnableVertexAttribArray(mMVPIndexHandle)
    }

    /**
     * draw the currently bound vertices in the vertex/index buffers
     *    USAGE: can only be called after calling bind() for this buffer.
     * @param primitiveType - the type of primitive to draw
     * @param offset - the offset in the vertex/index buffer to start at
     * @param numVertices - the number of vertices (indices) to draw
     */
    fun draw(primitiveType: Int, offset: Int, numVertices: Int) {
        if (indices != null) {                       // IF Indices Exist
            indices.position(offset)                  // Set Index Buffer to Specified Offset
            //draw indexed
            GLES20.glDrawElements(
                primitiveType, numVertices,
                GLES20.GL_UNSIGNED_SHORT, indices
            )
        } else {                                         // ELSE No Indices Exist
            //draw direct
            GLES20.glDrawArrays(primitiveType, offset, numVertices)
        }
    }


    /**
     * clear binding states when done rendering batches.
     *   USAGE: call once before calling draw() multiple times for this buffer.
     */
    fun unbind() {
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle)
    }

    companion object {

        enum class ComponentsInVertexPosition (val number:Int) {
            FOR_2D (2),
            FOR_3D (3)
        }

        // Number of Components in Vertex Color
        const val COLOR_CNT = 4

        const val COMPONENTS_IN_VERTEX_TEXURE_COORD = 2
        // Number of Components in Vertex Normal
        const val NORMAL_CNT = 3

        const val COMPONENTS_IN_MVP_MATRIX_INDEX = 1

        // Index Byte Size (Short.SIZE = bits)
        const val INDEX_SIZE = java.lang.Short.SIZE / 8

        const val TAG = "Vertices"
    }
}