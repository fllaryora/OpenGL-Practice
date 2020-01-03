package com.intecanar.openglpractice

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.intecanar.openglpractice.opengL.surface.OpenGLView
import android.hardware.SensorEventListener
import android.hardware.SensorManager




class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var gLView: OpenGLView
    private var mSensorManager : SensorManager ?= null
    private var mAccelerometer : Sensor ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get reference of the service
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // focus in accelerometer
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //quit status bar
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

        gLView = OpenGLView(this)
        setContentView(gLView)

    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {
        //super.onPointerCaptureChanged(hasCapture)
    }

    override fun onResume() {
        super.onResume()
        mSensorManager!!.registerListener(this,mAccelerometer,
            SensorManager.SENSOR_DELAY_GAME)
        this.gLView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mSensorManager!!.unregisterListener(this)
        this.gLView.onPause()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private val movingAverage = Array(2) { FloatArray(OVERFLOW_LIMIT) }
    private var overflow = 0
    override fun onSensorChanged(event: SensorEvent?) {
        if ( Sensor.TYPE_ACCELEROMETER == event?.sensor?.type ) {
            val x : Float = Math.round(event.values[X_AXIS] * 100.0) / 100f
            val y : Float = Math.round(event.values[Y_AXIS] * 100.0) / 100f

            movingAverage[0][overflow] = x
            movingAverage[1][overflow] = y

            val s1 = movingAverage[0].average().toFloat() //*-1f
            val s2 = movingAverage[1].average().toFloat() *-1f
            if (this.gLView.renderer?.objectsReady == true) {
                this.gLView.renderer?.getCircle()?.calculatePoints(s1 / SCALE, s2 / SCALE, 0.05f, 75)
                this.gLView.requestRender()
            }
            overflow += 1
            if (overflow >= OVERFLOW_LIMIT) {
                overflow = 0
            }
        }

    }

    companion object{
        const val X_AXIS = 1
        const val Y_AXIS = 0
        const val Z_AXIS = 2
        const val SCALE = 4
        const val OVERFLOW_LIMIT = 20
    }
}
