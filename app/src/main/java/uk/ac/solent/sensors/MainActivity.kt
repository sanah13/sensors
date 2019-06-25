package uk.ac.solent.sensors
import android.content.Context
import android.content.DialogInterface
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
import android.support.v7.app.AlertDialog
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

    private val TAG = "Permission"
    private val REQUEST_RECORD_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionSetting()

        image = findViewById(R.id.img1) as ImageView
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accel = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        magnet = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)
    }
    private fun permissionSetting() {
        val permission= ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "Permission denied")
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Permission for microphone access is required for this app")
            builder.setTitle("Permission Required")
            builder.setPositiveButton("ok")
            {
               dialog, which->
                Log.d(TAG,"Clicked")
                makeRequest()
            }
            makeRequest()
            val dialog= builder.create()
            dialog.show()
        }
        else {
            makeRequest()
        }
    }
    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_RECORD_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_RECORD_CODE-> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG, "Permission has been denied")
                }
                else
                {
                    Log.d(TAG, "Permission has been granted")
                }

            }
        }
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
            DegreesInputOutput(event.values, accelValues)
            accel2 = true
        } else if (event.sensor === magnet) {
            DegreesInputOutput(event.values, magValues)
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
    fun DegreesInputOutput (input: FloatArray, output: FloatArray) {
        val degree = 0.05f
        for (i in input.indices) {
            output[i] = output[i] + degree * (input[i] - output[i])
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




