package uk.ac.solent.sensors

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_settings.*
import android.graphics.PorterDuff



class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

//        val RadioGroup = findViewById<RadioGroup>(R.id.r1)
//        RadioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener(radioGroup, i ->
//        when(i) {
//            R.id.red -> window.decorView.setBackgroundColor(Color.parseColor(#FF0000))
//            R.id.blue -> window.decorView.setBackgroundColor(Color.parseColor(#FFFF00))
//            R.id.green -> window.decorView.setBackgroundColor(Color.parseColor(#FFFF00))
//        }
//

    }
}
