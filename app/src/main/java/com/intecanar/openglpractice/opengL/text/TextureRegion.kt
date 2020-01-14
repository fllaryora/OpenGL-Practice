package com.intecanar.openglpractice.opengL.text

/**
 * calculate U,V coordinates from specified texture coordinates
 * @param textureWidth, textureHeight - the width and height of the texture the region is for
 * @param x, y - the top/left (x,y) of the region on the texture (in pixels)
 * @param width, height - the width and height of the region on the texture (in pixels)
 */
class TextureRegion (textureWidth: Float, textureHeight: Float,
                     x: Float, y: Float,
                     width: Float, height: Float) {

    // Top/Left U,V Coordinates
    val u1: Float = x / textureWidth
    val v1: Float = y / textureHeight
    // Bottom/Right U,V Coordinates
    val u2: Float = this.u1 + width / textureWidth
    val v2: Float = this.v1 + height / textureHeight

}