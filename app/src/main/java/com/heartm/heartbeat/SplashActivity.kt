package com.heartm.heartbeat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity()
{
    private val SHOW_DURATION:Long=3000

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler()

        handler.postDelayed(Runnable
        {
            val sm = SessionManager(this@SplashActivity)

            if (sm.isLoggedIn()) {
                sm.createUserProfiles()
                 startActivity(Intent(this, DashboardActvity::class.java))
            }else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        },SHOW_DURATION)

    }
}
