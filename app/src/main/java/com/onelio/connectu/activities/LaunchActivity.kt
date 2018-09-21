package com.onelio.connectu.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.onelio.connectu.App
import com.onelio.connectu.R
import com.onelio.connectu.utils.EnvironmentHelper
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import com.onelio.connectu.api.LoginRequest


class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        findViewById<TextView>(R.id.version).text = EnvironmentHelper.AppVersion(this)

        // Animate logo - Scale Up
        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.anim_scale_up)
        anim.setAnimationListener(animationListener)
        findViewById<ImageView>(R.id.launcher_logo).startAnimation(anim)
    }

    private val animationListener = object: Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            // We must set login request initialization
            // inside the animation-complete triggers to
            // ensure the animations will overlap.
            val app = application as App
            LoginRequest(app, sessionListener)
        }
    }

    val sessionListener = object: LoginRequest.LoginResponse {
        override fun onCompletion() {
            val app = application as App
            if (!app.isValidAccount()) {
                startLoginActivity()
            } else {
                if (app.isConnected()) {
                    startMainActivity()
                } else {
                    //TODO login attempt
                }
            }
        }
    }

    fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val imageView = findViewById<View>(R.id.launcher_logo)
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
        val pair1 = Pair.create(imageView, getString(R.string.activity_login_image_trans))

        Handler(Looper.getMainLooper()).post {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1)
            startActivity(intent, options.toBundle())
        }
    }

    fun startMainActivity() {
        Handler(Looper.getMainLooper()).post {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
