package com.intecanar.openglpractice.opengL.text

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.opengl.GLES20
import android.opengl.Matrix
import com.intecanar.openglpractice.opengL.color.Color
import com.intecanar.openglpractice.opengL.renderer.program.BatchTextProgram
import com.intecanar.openglpractice.opengL.surface.OpenGLView

class GLText ( context: Context, typeface: Typeface, textSize: Int, padX: Int, padY: Int ) {

    private val mProgram = BatchTextProgram(context = context)
    private val mColorHandle = GLES20.glGetUniformLocation(mProgram.handle, "u_Color")
    private val mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram.handle, "u_Texture")
    private val batchRenderer = SpriteBatchRenderer(mProgram)

    private val textFontGL = TextFontGL(fontPadXInPx = padX, fontPadYInPx = padY,
        typeface = typeface, textSize = textSize)

    private val textureSize: Int = when {
        this.textFontGL.getMaxCellSize() <= 24 -> 256
        this.textFontGL.getMaxCellSize() <= 40 -> 512
        this.textFontGL.getMaxCellSize() <= 80 -> 1024
        else -> 2048
    }

    private val textureId: Int

    var scaleX: Float = UNSCALED
    var scaleY: Float = UNSCALED
    var space: Float = UNSCALED_SPACE


    private val fullTextureRegion: TextureRegion

    private val regionOfEachCharacter: Array<TextureRegion>

    //private var cellWidth: Int = 0
    //private var cellHeight: Int = 0
    private val columnsCount: Int = this.textureSize / this.textFontGL.getCellWidth()
    private val rowsCount: Int = Math.ceil((CHAR_COUNT.toFloat() / columnsCount.toFloat()).toDouble()).toInt()


    init {
        textureId = createTextureId()
        regionOfEachCharacter = createTextureRegionForEachCharacter()

        // create full texture region
        fullTextureRegion = TextureRegion(
            textureSize.toFloat(), textureSize.toFloat(), 0f, 0f,
            textureSize.toFloat(), textureSize.toFloat())

    }

     private fun createTextureRegionForEachCharacter() :  Array<TextureRegion> {
         val region = mutableListOf<TextureRegion>()
         // setup the array of character texture regions
         var startXPosition = 0f
         var startYPosition = 0f
         val width = this.textFontGL.getCellWidth().toFloat()
         val height = this.textFontGL.getCellHeight().toFloat()
         val size = this.textureSize.toFloat()
         for (charOrderIndex in 0 until CHAR_COUNT) {
             region.add(TextureRegion(size, size, startXPosition, startYPosition, width - 1f, height - 1f))
             startXPosition += width
             if (endOfTextureReached(startXPosition, width)) {
                 startXPosition = 0f
                 startYPosition += height
             }
         }
         return region.toTypedArray()
     }

    private fun endOfTextureReached(startXPosition: Float, width: Float) =
        startXPosition + width > textureSize

    private fun createTextureId() : Int {
        // create an empty bitmap (alpha only)
        val bitmap =
            Bitmap.createBitmap(this.textureSize, this.textureSize, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        bitmap.eraseColor(COLOR_TRANSPARENT)

        this.textFontGL.also {
            // render each of the characters to the canvas (ie. build the font map)
            var startXPosition = it.fontPadXInPx.toFloat()
            var startYPosition = it.getCellHeight().toFloat() - 1f - it.getFontDescentInPx() - it.fontPadYInPx.toFloat()
            val charArray = CharArray(2)
            for (character in FIRST_CHARACTER_ASCII..LAST_CHARACTER_ASCII) {

                charArray[0] = character.toChar()
                canvas.drawText(charArray, 0, 1, startXPosition, startYPosition, it.getPaint())

                startXPosition += it.getCellWidth().toFloat()

                if (endOfLineReached(startXPosition, it)) {
                    startXPosition = this.textFontGL.fontPadXInPx.toFloat()
                    startYPosition += this.textFontGL.getCellHeight().toFloat()
                }
            }
        }
        return TextureHelper.loadTexture(bitmap)
    }

    private fun endOfLineReached(
        startXPosition: Float,
        it: TextFontGL
    ) = startXPosition + it.getCellWidth() - it.fontPadXInPx > textureSize

    fun beginWhiteOpaque(vpMatrix: FloatArray) {
        beginWhiteWithAlpha( 1.0f, vpMatrix)
    }

    fun beginWhiteWithAlpha(alpha: Float, vpMatrix: FloatArray) {
        val color = Color(red = 1.0f, green =  1.0f, blue = 1.0f, alpha = alpha)
        beginTextDrawing(color, vpMatrix)
    }

    /**
     * 	  @param vpMatrix - View and projection matrix to use
     */
    fun beginTextDrawing(color: Color, vpMatrix: FloatArray) {
        initDraw(color)
        batchRenderer.beginBatch(vpMatrix)
    }

    private fun initDraw(color: Color) {
        GLES20.glUseProgram(mProgram.handle) // specify the program to use

        // set color TODO: only alpha component works, text is always black #BUG

        GLES20.glUniform4fv(mColorHandle, 1, color.getArray(), 0)
        GLES20.glEnableVertexAttribArray(mColorHandle)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)  // Set the active texture unit to texture unit 0

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId) // Bind the texture to this unit

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0
        GLES20.glUniform1i(mTextureUniformHandle, 0)
    }

    fun end() {
        batchRenderer.endBatch()                               // End Batch
        GLES20.glDisableVertexAttribArray(mColorHandle)
    }

    /**
     * draw text at the specified x,y position
     * @param text - the string to draw
     * @param x, y, z - the x, y, z position to draw text at (bottom left of text; including descent)
     * @param angleDeg - angle to rotate the text
     */
    fun draw( text: String, x: Float, y: Float, z: Float,
        angleDegX: Float, angleDegY: Float, angleDegZ: Float ) {
        val characterHeight = this.textFontGL.getCellHeight() * scaleY
        val characterWidth = this.textFontGL.getCellWidth() * scaleX
        val textLength = text.length
        val adjustStartX = x + characterWidth / 2.0f - this.textFontGL.fontPadXInPx * scaleX  // Adjust Start X
        val adjustStartY = y +characterHeight / 2.0f - this.textFontGL.fontPadYInPx * scaleY  // Adjust Start Y

        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, adjustStartX, adjustStartY, z)
        Matrix.rotateM(modelMatrix, 0, angleDegZ, 0f, 0f, 1f)
        Matrix.rotateM(modelMatrix, 0, angleDegX, 1f, 0f, 0f)
        Matrix.rotateM(modelMatrix, 0, angleDegY, 0f, 1f, 0f)

        var letterX = 0f
        val letterY = 0f

        for (characterIndex in 0 until textLength) {              // FOR Each Character in String
            var regionIndex = text[characterIndex].toInt() - FIRST_CHARACTER_ASCII
            if (regionIndex < 0 || regionIndex >= CHAR_COUNT) {
                regionIndex = 0
            }

            batchRenderer.drawSprite( letterX, letterY, characterWidth, characterHeight,
                regionOfEachCharacter[regionIndex], modelMatrix )
            val width = this.textFontGL.getCharacterwidth(text[characterIndex])
            letterX += (width + space) * scaleX
        }
    }


    fun draw(text: String, x: Float, y: Float, z: Float = 0f, angleDegZ: Float = 0f) {
        draw(text, x, y, z, 0f, 0f, angleDegZ)
    }

    fun draw(text: String, x: Float, y: Float, angleDeg: Float) {
        draw(text, x, y, 0f, angleDeg)
    }

    /**
     * Draw Text Centered
     * draw text CENTERED at the specified x,y position
     * text - the string to draw
     * @param x, y, z - the x, y, z position to draw text at (bottom left of text)
     * @param angleDeg - angle to rotate the text
     * @return the total width of the text that was drawn
     */
    fun drawC( text: String, x: Float, y: Float, z: Float,
        angleDegX: Float, angleDegY: Float, angleDegZ: Float): Float {
        val len = getLengthInPx(text)
        draw( text, x - len / 2.0f, y - getCharHeight() / 2.0f, z,
            angleDegX, angleDegY, angleDegZ )
        return len
    }

    fun drawC(text: String, x: Float, y: Float, z: Float, angleDegZ: Float): Float {
        return drawC(text, x, y, z, 0f, 0f, angleDegZ)
    }

    fun drawC(text: String, x: Float, y: Float, angleDeg: Float): Float {
        return drawC(text, x, y, 0f, angleDeg)
    }

    fun drawC(text: String, x: Float, y: Float): Float {
        val len = getLengthInPx(text)
        return drawC(text, x - len / 2.0f, y - getCharHeight() / 2.0f, 0f)

    }

    /**
     * Draw Text Centered (X-Axis Only)
     * @return length
     */
    fun drawCX(text: String, x: Float, y: Float): Float {
        val len = getLengthInPx(text)
        draw(text, x - len / 2.0f, y)
        return len
    }

    /**
     * Draw Text Centered (Y-Axis Only)
     */
    fun drawCY(text: String, x: Float, y: Float) {
        draw(text, x, y - getCharHeight() / 2.0f)
    }

    /**
     * Draw Font Texture
     *  draw the entire font texture (NOTE: for testing purposes only)
     * @param vpMatrix - View and projection matrix to use
     */
    fun drawTexture(vpMatrix: FloatArray) {
        val colorWhite = Color (red = 1f, green = 1f, blue = 1f, alpha = 1f)
        initDraw(colorWhite)

        batchRenderer.beginBatch(vpMatrix)                  // Begin Batch (Bind Texture)
        val idMatrix = FloatArray(16)
        Matrix.setIdentityM(idMatrix, 0)
        batchRenderer.drawSprite(
            ((OpenGLView.width / 2) - textureSize / 2).toFloat(), ((OpenGLView.height / 2) - textureSize / 2).toFloat(),
            textureSize.toFloat(), textureSize.toFloat(), fullTextureRegion, idMatrix
        )  // Draw
        batchRenderer.endBatch()                               // End Batch
    }

    companion object {

        const val FIRST_CHARACTER_ASCII = 32
        const val LAST_CHARACTER_ASCII = 126
        const val CHAR_COUNT = LAST_CHARACTER_ASCII - FIRST_CHARACTER_ASCII

        const val COLOR_TRANSPARENT = 0x00000000

        const val UNSCALED = 1f
        const val UNSCALED_SPACE = 0f
        private val TAG = "GLTEXT"
    }

    /**
     * Font metrics
     */
    val ascent: Float
        get() = this.textFontGL.getFontAscentInPx() * scaleY

    val descent: Float
        get() = this.textFontGL.getFontDescentInPx() * scaleY

    val height: Float
        get() = this.textFontGL.getFontHeightInPx() * scaleY

    fun getCharWidthMax(): Float {
        return this.textFontGL.getCharWidthMaxInPx() * scaleX
    }

    fun getCharHeight(): Float {
        return this.textFontGL.getFontHeightInPx() * scaleY
    }

    fun getCharWidth(chr: Char): Float {
        return this.textFontGL.getCharacterwidth(chr) * scaleX
    }

    fun setScale(scale: Float) {
        scaleY = scale
        scaleX = scaleY                        // Set Uniform Scale
    }

    fun setScale(sx: Float, sy: Float) {
        scaleX = sx                                    // Set X Scale
        scaleY = sy                                    // Set Y Scale
    }

    fun getLengthInPx(text: String): Float {

        var spaceLength = if (text.length > 1) {
            (text.length - 1) * space * scaleX
        } else {
            0.0f
        }
        return this.textFontGL.getStringBounds(text) * scaleX + spaceLength
    }

}
