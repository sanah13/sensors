package uk.ac.solent.sensors
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {
    var currentDegree = 0.0f
    lateinit var SensorMngr: SensorManager
    lateinit var magnet: Sensor
    lateinit var accel: Sensor
    lateinit var image: ImageView
    var accelValues = FloatArray(3)
    var magValues = FloatArray(3)
    var orientationMatrix = FloatArray(16)
    var accel2 = false
    var magnet2 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        image = R.id.img1 as ImageView
        val SensorMngr = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accel = SensorMngr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnet = SensorMngr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        SensorMngr.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
    }
    override fun onResume() {
        super.onResume()
        SensorMngr.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME)
        SensorMngr.registerListener(this, magnet, SensorManager.SENSOR_DELAY_GAME)
    }
    override fun onPause() {
        super.onPause()
        SensorMngr.unregisterListener(this, accel)
        SensorMngr.unregisterListener(this, magnet)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor === accel) {
            lowPass(event.values, accelValues)
            accel2 = true
        } else if (event.sensor === magnet) {
            lowPass(event.values, magValues)
            magnet2 = true
        }

        if (accel2 && magnet2) {
            val rotation = FloatArray(9)
            if (SensorManager.getRotationMatrix(rotation, null, accelValues, magValues)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotation, orientation)
                val degree = (Math.toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360

                val rotateAnimation = RotateAnimation(
                        currentDegree, -degree,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f)
                rotateAnimation.duration = 1000
                rotateAnimation.fillAfter = true

                image.startAnimation(rotateAnimation)
                currentDegree = -degree
                if (event.sensor == accel) {
                    tv5.text = event.values[0].toString()
                    tv6.text = event.values[1].toString()
                    tv7.text = event.values[2].toString()
                    accelValues = event.values.clone()
                } else if (event.sensor == magnet) {
                    tv3.text = event.values[0].toString()
                    tv2.text = event.values[1].toString()
                    tv4.text = event.values[2].toString()
                    magValues = event.values.clone()
                }
                SensorManager.getRotationMatrix(orientationMatrix, null, accelValues, magValues)
            }
        }
    }
               fun lowPass(input: FloatArray, output: FloatArray) {
                    val alpha = 0.05f
                    for (i in input.indices) {
                        output[i] = output[i] + alpha * (input[i] - output[i])
                    }
            }
        }



