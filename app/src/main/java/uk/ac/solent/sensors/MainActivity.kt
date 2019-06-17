package uk.ac.solent.sensors
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_GAME
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Math.toDegrees
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), SensorEventListener {
    var DegreeValue = 0.0f
    lateinit var sensorManager: SensorManager
    lateinit var magnet: Sensor
    lateinit var accel: Sensor
    lateinit var image: ImageView
    var accelValues = FloatArray(3)
    var magValues = FloatArray(3)
    var orientationMatrix = FloatArray(16)
    var accel2 = false
    var mag2 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        image = findViewById(R.id.img1) as ImageView
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accel = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        magnet = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)
        }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(this, accel, SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnet, SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this, accel)
        sensorManager.unregisterListener(this, magnet)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == accel) {
            tv5.text = event.values[0].toString()
            tv6.text = event.values[1].toString()
            tv7.text = event.values[2].toString()
            accelValues = event.values.clone()
        }
        else if (event.sensor == magnet) {
            tv3.text = event.values[0].toString()
            tv2.text = event.values[1].toString()
            tv4.text = event.values[2].toString()
            magValues = event.values.clone()
        }

        if (event.sensor === accel) {
            lowPass(event.values, accelValues)
            accel2 = true
        } else if (event.sensor === magnet) {
            lowPass(event.values, magValues)
            mag2 = true
        }
        if (accel2 && mag2) {
            val r = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, null, accelValues, magValues)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                val degree = (toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360

                val rotateAnimation = RotateAnimation(
                        DegreeValue, -degree,
                        RELATIVE_TO_SELF, 0.5f,
                        RELATIVE_TO_SELF, 0.5f)
                rotateAnimation.duration = 1000
                rotateAnimation.fillAfter = true

                image.startAnimation(rotateAnimation)
                DegreeValue = -degree
            }
                SensorManager.getRotationMatrix(orientationMatrix, null, accelValues, magValues)
            }
        }
    fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f

        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?):Boolean {
        when (item?.itemId) {
            R.id.Settings -> {
                var clickintent = Intent(this, SettingsActivity::class.java)
                startActivity(clickintent)
            }
            else ->
                super.onOptionsItemSelected(item)
        }
        return true;
    }
}


