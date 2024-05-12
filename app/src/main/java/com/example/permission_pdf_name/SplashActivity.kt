package com.example.permission_pdf_name

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar

class SplashActivity : AppCompatActivity() {
    private lateinit var letsstart : Button
    lateinit var progressBar: ProgressBar
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        letsstart = findViewById(R.id.letsstart)
        progressBar = findViewById(R.id.load)
        loadprogressbar()
        letsstart.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

    }
    private fun loadprogressbar(){
        val timer = object : CountDownTimer(5000, 1000) { // 5000 milliseconds = 5 seconds
            override fun onTick(millisUntilFinished: Long) {
                // Update the progress bar status here if needed
            }

            override fun onFinish() {
                // Hide the progress bar and show the button
                progressBar.visibility = ProgressBar.INVISIBLE
                letsstart.visibility = Button.VISIBLE
            }
        }
        timer.start()

    }
}