package com.intecanar.openglpractice.opengL.text

import android.graphics.Paint
import android.graphics.Typeface

class TextFontGL (val typeface: Typeface, val fontPadXInPx: Int, val fontPadYInPx: Int, val textSize: Int ) {

    private val paint : Paint
    private val fontHeightInPx: Float
    private val fontAscentInPx: Float
    private val fontDescentInPx: Float
    private val charWidthMaxInPx: Float

     init {
         this.paint = Paint().also {
             it.isAntiAlias = true
             it.textSize = textSize.toFloat()
             //(White, Opaque)
             it.color = -0x1
             it.typeface = typeface
         }
         this.paint.fontMetrics.also {
             this.fontHeightInPx = Math.ceil((Math.abs(it.bottom) + Math.abs(it.top)).toDouble()).toFloat()
             this.fontAscentInPx = Math.ceil(Math.abs(it.ascent).toDouble()).toFloat()
             this.fontDescentInPx = Math.ceil(Math.abs(it.descent).toDouble()).toFloat()
         }

         this.charWidthMaxInPx = calculateMaxWidth()

         if (getMaxCellSize() < FONT_SIZE_MIN_IN_PX || getMaxCellSize() > FONT_SIZE_MAX_IN_PX) {
             throw RuntimeException("Error loading texture.")
         }

     }

    private fun calculateMaxWidth() = (FIRST_CHARACTER_ASCII..LAST_CHARACTER_ASCII).fold(0f) {
            maximun:Float, eachCharacter ->
            val width = getCharacterwidth(eachCharacter)
             Math.max( width, maximun)
        }

    private fun getCharacterBounds(character: Int):FloatArray {
        return getCharacterBounds(character.toChar())
    }

    private fun getCharacterBounds(character: Char):FloatArray {
        val index = 0
        val length = 1
        val charArray = CharArray(2)
        charArray [0] = character
        val widths = FloatArray(2)
        paint.getTextWidths(charArray, index, length, widths)

        return widths
    }

    fun getStringBounds(text: String):Float {
        val start = 0
        val end = 1
        return text.fold(0f) { width, character ->
            val widths = FloatArray(2)
            val charArray = CharArray(2)
            charArray [0] = character
            paint.getTextWidths(charArray, start, end, widths)
            width + widths[0]
        }

    }

    fun getCharacterwidth(character: Char):Float {
        return getCharacterBounds(character)[0]
    }

    fun getCharacterwidth(character: Int):Float {
        return getCharacterBounds(character)[0]
    }

    fun getFontHeightInPx() = this.fontHeightInPx
    fun getFontAscentInPx() = this.fontAscentInPx
    fun getFontDescentInPx() = this.fontDescentInPx
    fun getCharWidthMaxInPx() = this.charWidthMaxInPx

    fun getCellWidth() = this.charWidthMaxInPx.toInt() + 2 * this.fontPadXInPx
    fun getCellHeight() = this.fontHeightInPx.toInt() + 2 * this.fontPadYInPx
    fun getMaxCellSize() = Math.max(getCellWidth(), getCellHeight())

    fun getPaint() = this.paint

    companion object {

        const val FIRST_CHARACTER_ASCII = 32
        const val LAST_CHARACTER_ASCII = 126

        const val FONT_SIZE_MIN_IN_PX = 6
        const val FONT_SIZE_MAX_IN_PX = 180

        private val TAG = "TextFontGL"
    }
}