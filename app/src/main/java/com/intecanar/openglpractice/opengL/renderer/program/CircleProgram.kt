package com.intecanar.openglpractice.opengL.renderer.program

import com.intecanar.openglpractice.opengL.renderer.AttribVariable

class CircleProgram : Program() {

    fun init() {
        super.init(vertexShaderCode, fragmentShaderCode, programVariables)
    }

    companion object {

        private val programVariables = arrayOf(
            AttribVariable.V_Position
        )
        // for rendering the vertices of a shape.
        private val vertexShaderCode = (
                "attribute vec4 vPosition;" +
                        "void main() {" +
                        "  gl_Position = vPosition;" +
                        "}")

        //for rendering the face of a shape with colors or textures.
        private val fragmentShaderCode = (
                "precision highp float;" + //"precision mediump float;" +
                        "uniform vec4 vColor;" +
                        "void main() {" +
                        "  gl_FragColor = vColor;" +
                        "}")

    }

}