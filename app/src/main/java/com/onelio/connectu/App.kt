package com.onelio.connectu

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.core.CrashlyticsCore
import com.google.gson.Gson
import com.onelio.connectu.types.Account
import com.onelio.connectu.utils.Settings

class App : Application() {
    /**
     * Usually called when an activity wants to know the current
     * account data or the requests want to use cookies.
     */
    private var account = Account()
    val getAccount = fun() : Account {return account}

    /**
     * This bool will tell if there is a valid account(user and password exists)
     * and will be set by the loader function. Normally in the onCreate.
     */
    private var validAccount : Boolean = false
    val isValidAccount = fun() : Boolean {return validAccount}
    val setValidAccount = fun(validAccount : Boolean) {this.validAccount = validAccount}

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
        Fabric.with(this, crashlyticsKit)

        //Reload user-data
        reloadAccount()
    }

    fun reloadAccount() {
        val jaccount = Settings(baseContext).getString(CONFIG_OBJECT_ACCOUNT)
        if (jaccount.isEmpty()) {
            return
        }
        Gson().fromJson(jaccount, Account::class.java)
        validAccount = true
    }
}