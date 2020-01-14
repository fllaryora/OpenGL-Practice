package com.intecanar.openglpractice.opengL.shapes


import com.intecanar.openglpractice.opengL.color.Color
import com.intecanar.openglpractice.opengL.surface.OpenGLView
import kotlin.math.cos
import kotlin.math.sin

data class Shape constructor( val  centerPoint: Pair<Float,Float>,
                         val radius : Float,
                         val segments : Int, val color : Color
) {

    fun getCoordinates() : FloatArray {
        val coordinateSize = this.segments * COORDS_PER_VERTEX
        val coordinates = FloatArray(coordinateSize)

        for (coordinateIndex in 0..(coordinateSize - COORDS_PER_VERTEX) step COORDS_PER_VERTEX) {

            val percent : Float = (coordinateIndex.toFloat() / (this.segments.toFloat() - 1f))
            val percentInRadians : Float = 2f * percent * Math.PI.toFloat()

            //val aspectRatio =
            val aspectRatio = OpenGLView.aspectRatio
            //Vertex position
            val xi = this.centerPoint.first + this.radius * cos(percentInRadians)
            val yi = this.centerPoint.second + this.radius * sin(percentInRadians) * aspectRatio

            coordinates[coordinateIndex] = xi
            coordinates[coordinateIndex+1] = yi
            coordinates[coordinateIndex+2] = 0f
        }
        return coordinates
    }

    companion object {
        const val COORDS_PER_VERTEX = 3
    }
}