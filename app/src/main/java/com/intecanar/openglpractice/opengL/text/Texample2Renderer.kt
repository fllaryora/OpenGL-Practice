package com.intecanar.openglpractice.opengL.text

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.content.Context
import android.graphics.Typeface
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.intecanar.openglpractice.opengL.color.Color
import com.intecanar.openglpractice.opengL.surface.OpenGLView

class Texample2Renderer( private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var glText: GLText
    private val mProjMatrix = FloatArray(16)
    private val mVMatrix = FloatArray(16)
    private val mVPMatrix = FloatArray(16)


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)

        val file = "Roboto-Regular.ttf"
        val textSize = 14
        val padX = 2
        val padY = 2
        // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
        val typeface = Typeface.createFromAsset(context.assets, file)
        /**
         * Why this code is here, insted of init:
         * because GL is a shit and in  onSurfaceCreated
         * recognice the fucking context
         */
        glText = GLText(context,  typeface, textSize, padX, padY)

        // enable texture + alpha blending
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun onDrawFrame(unused: GL10) {
        val clearMask = GLES20.GL_COLOR_BUFFER_BIT
        GLES20.glClear(clearMask)
        Matrix.multiplyMM(mVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0)

        // TEST: render the entire font texture
        glText.drawTexture( mVPMatrix)
        glText.beginWhiteOpaque(mVPMatrix)

        glText.drawC("Test String 3D!", 0f, 0f, 0f, 0f, -30f, 0f)
        glText.draw("Diagonal 1", 40f, 40f, 40f)
        glText.draw("Column 1", 100f, 100f, 90f)

        glText.end()

        val colorBlue = Color(red = 0.0f, green =  0.0f, blue = 1.0f, alpha = 1.0f)
        glText.beginTextDrawing(colorBlue, mVPMatrix)

        glText.draw("More Lines...", 50f, 200f)        // Draw Test String
        glText.draw("The End.", 50f, 200 + glText.getCharHeight(), 180f)

        glText.end()
    }

    override fun onSurfaceChanged( unused: GL10, width: Int, height: Int) {

        GLES20.glViewport(0, 0, width, height)
        OpenGLView.width = width
        OpenGLView.height = height
        OpenGLView.aspectRatio = width.toFloat()/ height.toFloat()

        Matrix.frustumM(mProjMatrix, 0, -OpenGLView.aspectRatio,
            OpenGLView.aspectRatio, -1f, 1f, 1f, 10f)

        val useForOrtho = Math.min(width, height)

        //TODO: Is this wrong?
        Matrix.orthoM(
            mVMatrix, 0,
            (-useForOrtho / 2).toFloat(),
            (useForOrtho / 2).toFloat(),
            (-useForOrtho / 2).toFloat(),
            (useForOrtho / 2).toFloat(), 0.1f, 100f
        )
    }

    companion object {
        private val TAG = "TexampleRenderer"
    }
}