package com.onelio.connectu

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.core.CrashlyticsCore
import com.google.gson.Gson
import com.onelio.connectu.types.Account
import com.onelio.connectu.utils.UserSettings

class App : Application() {

    private var settings = UserSettings(baseContext)

    private var account = Account()
    val getAccount = fun() : Account {return account}

    private var connected : Boolean = false
    val isConnected = fun() : Boolean {return connected}
    val setConnected = fun(connected : Boolean) {this.connected = connected}

    private fun ReloadAccount() {
        val gson = Gson()
        val jacc = settings.getString(PREF_ACCOUNT)
        if (jacc.isEmpty()) return

        account = gson.fromJson(jacc, Account::class.java)
        connected = true
    }

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

        // Load user data
        ReloadAccount()
    }
}