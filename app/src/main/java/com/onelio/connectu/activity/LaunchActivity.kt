package com.onelio.connectu.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.onelio.connectu.R
import com.onelio.connectu.utils.EnvironmentHelper

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        findViewById<TextView>(R.id.version).text = EnvironmentHelper.AppVersion(this)

        // Animate logo - Scale Up
        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.anim_scale_up)
        findViewById<ImageView>(R.id.launcher_logo).startAnimation(anim)
    }
}
