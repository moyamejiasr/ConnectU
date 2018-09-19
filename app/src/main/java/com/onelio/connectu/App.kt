package com.onelio.connectu

import android.app.Application
import com.franmontiel.persistentcookiejar.ClearableCookieJar

class App : Application() {
    var cookieJar: ClearableCookieJar? = null
}