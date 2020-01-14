package com.intecanar.openglpractice.opengL.renderer.program

import android.content.Context
import com.intecanar.openglpractice.R
import com.intecanar.openglpractice.opengL.renderer.AttribVariable
import com.intecanar.openglpractice.opengL.renderer.getRawTextFile

class BatchTextProgram constructor(private val context : Context): Program() {

     init {
        val vertexShaderCode = this.context.resources.getRawTextFile(R.raw.text_vertex)
        val fragmentShaderCode = this.context.resources.getRawTextFile(R.raw.text_fragment)
        super.init(vertexShaderCode, fragmentShaderCode, programVariables)
    }

    companion object {
        private val programVariables = arrayOf(
            AttribVariable.A_Position, AttribVariable.A_TexCoordinate, AttribVariable.A_MVPMatrixIndex
        )
    }

}