package com.onelio.connectu.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.onelio.connectu.App
import com.onelio.connectu.R
import com.onelio.connectu.api.LoginRequest
import com.onelio.connectu.utils.EnvironmentHelper
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        findViewById<TextView>(R.id.version).text = EnvironmentHelper.AppVersion(this)

        // Animate logo - Scale Up
        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.anim_scale_up)
        findViewById<ImageView>(R.id.launcher_logo).startAnimation(anim)

        thread {
            sleep(2000)
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
