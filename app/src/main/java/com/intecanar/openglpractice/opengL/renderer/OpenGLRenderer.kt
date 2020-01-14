package com.intecanar.openglpractice.opengL.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.intecanar.openglpractice.opengL.color.Color
import com.intecanar.openglpractice.opengL.drawer.ShapeDrawer
import com.intecanar.openglpractice.opengL.shapes.Shape
import com.intecanar.openglpractice.opengL.surface.OpenGLView
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer constructor(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var  shapeDrawer: ShapeDrawer
    private var TAG = "OpenGLRenderer"
    var objectsReady = false

   fun getShape():ShapeDrawer = shapeDrawer


    override fun onDrawFrame(gl: GL10?) {
        // Redraw background color
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        shapeDrawer.draw()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        OpenGLView.width = width
        OpenGLView.height = height
        OpenGLView.aspectRatio = width.toFloat()/ height.toFloat()
    }

    override fun onSurfaceCreated(gl: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        // Set the background frame color
        GLES20.glClearColor(0.2f, 0.3f, 0.4f, 1.0f)

        // Create the GLText
       // glText =  GLText(context.assets)

        // Load the font from file (set size + padding), creates the texture
        // NOTE: after a successful call to this the font is ready for rendering!
       // glText.loadFont( "Roboto-Regular.ttf", 14, 2, 2 );  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
        val color = Color(0.5f, 0.5f, 0.5f, 1f)
        val shape = Shape(Pair(0f,0f), 0.10f, 360,  color)
        shapeDrawer = ShapeDrawer(listOf(shape), context)
        objectsReady = true
    }



}