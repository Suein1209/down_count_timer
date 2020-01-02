package com.suein1209.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.suein1209.countdowntimer.WCountDownTimerTextView
import com.suein1209.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    @Suppress("PrivatePropertyName")
    private val DEFAULT_TIME: Long = 1000 * 60 * 60 * 11

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.textTimerStart.setOnClickListener {
            binding.vTextCountdowntimer.cancel()
            binding.vTextCountdowntimer.start(DEFAULT_TIME, ::onTextViewTimerTimeOver)
        }
    }

    private fun onTextViewTimerTimeOver() {
        Toast.makeText(this, "TextView CountTimer Time OVER!", Toast.LENGTH_SHORT).show()
    }

}
