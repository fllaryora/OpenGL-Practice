package com.intecanar.openglpractice.opengL.renderer

enum class AttribVariable private constructor(val handle: Int, val attributeName: String) {
    A_Position(1, "a_Position"),
    A_TexCoordinate(2, "a_TexCoordinate"),
    A_MVPMatrixIndex(3, "a_MVPMatrixIndex"),
    V_Position(4, "vPosition")
}

enum class Uniform private constructor(val uniformName: String) {
    V_COLOR( "vColor")
}