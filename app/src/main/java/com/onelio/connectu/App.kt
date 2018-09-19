package com.onelio.connectu

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.core.CrashlyticsCore



class App : Application() {

    private var email : String = ""
    val getEmail = fun() : String {return email}

    private var password : String = ""
    val getPassword = fun() : String {return password}

    private var connected : Boolean = false
    val isConnected = fun() : Boolean {return connected}
    val setConnected = fun(connected : Boolean) {this.connected = connected}

    /**
     * We override onCreate from the Application to allow it to
     * have a working Crashlytics working at runtime. It's configured
     * to work only on release so will be ignored in debug.
     */
    override fun onCreate() {
        super.onCreate()
        // Set up Crashlytics, disabled for debug builds
        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this, crashlyticsKit)
    }
}