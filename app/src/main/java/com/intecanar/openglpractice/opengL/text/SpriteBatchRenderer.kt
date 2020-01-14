package com.intecanar.openglpractice.opengL.text

import com.intecanar.openglpractice.opengL.renderer.program.Program
import android.opengl.GLES20
import android.opengl.Matrix

class SpriteBatchRenderer (program: Program ) {


    private val maximumSpritesAllowedInBuffer: Int = CHAR_BATCH_SIZE
    private val vertexBuffer: FloatArray = FloatArray(maximumSpritesAllowedInBuffer
                                                    * VERTICES_PER_SPRITE * VERTEX_SIZE)

    private val vertices: Vertices = Vertices(
        maximumSpritesAllowedInBuffer * VERTICES_PER_SPRITE,
        maximumSpritesAllowedInBuffer * INDICES_PER_SPRITE,
        Vertices.Companion.ComponentsInVertexPosition.FOR_2D)

    private var vertexBufferStartIndex: Int = 0
    private var spritesCurrentlyInBuffer: Int = 0
    // View and projection matrix specified at beginWhiteWithAlpha
    private var mVPMatrix: FloatArray? = null
    // MVP matrix array to pass to shader
    private val uMVPMatrices = FloatArray(CHAR_BATCH_SIZE * 16)
    // shader handle of the MVP matrix array
    private val mMVPMatricesHandle: Int
    // used to calculate MVP matrix of each sprite
    private val mMVPMatrix = FloatArray(16)

    init {

        val tempIndexBuffer = ShortArray(maximumSpritesAllowedInBuffer * INDICES_PER_SPRITE)
        val len = tempIndexBuffer.size
        var j: Short = 0
        var i = 0
        while (i < len) {
            setIndexSetPerSprite(tempIndexBuffer, i, j)
            i += INDICES_PER_SPRITE
            j = (j + VERTICES_PER_SPRITE).toShort()
        }
        vertices.setIndexBufferForRendering(tempIndexBuffer, 0, len)
        mMVPMatricesHandle = GLES20.glGetUniformLocation(program.handle, "u_MVPMatrix")
    }

    private fun setIndexSetPerSprite(indices: ShortArray, i: Int, j: Short) {
        indices[i + 0] = (j + 0).toShort()
        indices[i + 1] = (j + 1).toShort()
        indices[i + 2] = (j + 2).toShort()
        indices[i + 3] = (j + 2).toShort()
        indices[i + 4] = (j + 3).toShort()
        indices[i + 5] = (j + 0).toShort()
    }

    fun beginBatch(vpMatrix: FloatArray) {
        spritesCurrentlyInBuffer = 0                                 // Empty Sprite Counter
        vertexBufferStartIndex = 0                                // Reset Buffer Index (Empty)
        mVPMatrix = vpMatrix
    }

    /**
     *  signal the end of a batch. render the batched sprites
     */
    fun endBatch() {
        if (hasSpritesToRender()) {
            // bind MVP matrices array to shader
            GLES20.glUniformMatrix4fv(mMVPMatricesHandle, spritesCurrentlyInBuffer, false, uMVPMatrices, 0)
            GLES20.glEnableVertexAttribArray(mMVPMatricesHandle)

            vertices.setVertices(vertexBuffer, 0, vertexBufferStartIndex)  // Set Vertices from Buffer
            vertices.bind()
            vertices.draw(GLES20.GL_TRIANGLES, 0, spritesCurrentlyInBuffer * INDICES_PER_SPRITE)
            vertices.unbind()
        }
    }

    private fun hasSpritesToRender() = spritesCurrentlyInBuffer > 0

    /**
     * Draw Sprite to Batch
     * batch specified sprite to batch. adds vertices for sprite to vertex buffer
     *    NOTE: MUST be called after beginBatch(), and before endBatch()!
     *    NOTE: if the batch overflows, this will render the current batch, restart it,
     *          and then batch this sprite.
     * @param x, y - the x,y position of the sprite (center)
     * @param width, height - the width and height of the sprite
     * @param region - the texture region to use for sprite
     * @param modelMatrix - the model matrix to assign to the sprite
     */
    fun drawSprite(x: Float, y: Float, width: Float, height: Float,
        region: TextureRegion, modelMatrix: FloatArray) {
        if (isSpriteBufferFull()) {
            endBatch()
            // NOTE: leave current texture bound!!
            // Empty Sprite Counter
            spritesCurrentlyInBuffer = 0
            // Reset Buffer Index (Empty)
            vertexBufferStartIndex = 0
        }

        val halfWidth = width / 2.0f                 // Calculate Half Width
        val halfHeight = height / 2.0f               // Calculate Half Height
        val x1 = x - halfWidth                       // Calculate Left X
        val y1 = y - halfHeight                      // Calculate Bottom Y
        val x2 = x + halfWidth                       // Calculate Right X
        val y2 = y + halfHeight                      // Calculate Top Y

        vertexBuffer[vertexBufferStartIndex++] = x1               // Add X for Vertex 0
        vertexBuffer[vertexBufferStartIndex++] = y1               // Add Y for Vertex 0
        vertexBuffer[vertexBufferStartIndex++] = region.u1        // Add U for Vertex 0
        vertexBuffer[vertexBufferStartIndex++] = region.v2        // Add V for Vertex 0
        vertexBuffer[vertexBufferStartIndex++] = spritesCurrentlyInBuffer.toFloat()

        vertexBuffer[vertexBufferStartIndex++] = x2               // Add X for Vertex 1
        vertexBuffer[vertexBufferStartIndex++] = y1               // Add Y for Vertex 1
        vertexBuffer[vertexBufferStartIndex++] = region.u2        // Add U for Vertex 1
        vertexBuffer[vertexBufferStartIndex++] = region.v2        // Add V for Vertex 1
        vertexBuffer[vertexBufferStartIndex++] = spritesCurrentlyInBuffer.toFloat()

        vertexBuffer[vertexBufferStartIndex++] = x2               // Add X for Vertex 2
        vertexBuffer[vertexBufferStartIndex++] = y2               // Add Y for Vertex 2
        vertexBuffer[vertexBufferStartIndex++] = region.u2        // Add U for Vertex 2
        vertexBuffer[vertexBufferStartIndex++] = region.v1        // Add V for Vertex 2
        vertexBuffer[vertexBufferStartIndex++] = spritesCurrentlyInBuffer.toFloat()

        vertexBuffer[vertexBufferStartIndex++] = x1               // Add X for Vertex 3
        vertexBuffer[vertexBufferStartIndex++] = y2               // Add Y for Vertex 3
        vertexBuffer[vertexBufferStartIndex++] = region.u1        // Add U for Vertex 3
        vertexBuffer[vertexBufferStartIndex++] = region.v1        // Add V for Vertex 3
        vertexBuffer[vertexBufferStartIndex++] = spritesCurrentlyInBuffer.toFloat()

        // add the sprite mvp matrix to uMVPMatrices array

        Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, modelMatrix, 0)

        //TODO: make sure spritesCurrentlyInBuffer < 24
        for (i in 0..15) {
            uMVPMatrices[spritesCurrentlyInBuffer * 16 + i] = mMVPMatrix[i]
        }

        spritesCurrentlyInBuffer++
    }

    private fun isSpriteBufferFull() = spritesCurrentlyInBuffer == maximumSpritesAllowedInBuffer

    companion object {

        // Vertex Size (in Components) ie. (X,Y,U,V,M), M is MVP matrix index
        private val VERTEX_SIZE = 5
        private val VERTICES_PER_SPRITE : Int = 4
        private val INDICES_PER_SPRITE = 6
        private val TAG = "SpriteBatchRenderer"
        /**
         * Number of Characters to Render Per Batch
         * must be the same as the size of u_MVPMatrix
         * Maximum Sprites Allowed in Buffer in BatchTextProgram
         */
        private val CHAR_BATCH_SIZE = 24
    }
}