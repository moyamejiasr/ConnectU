package com.onelio.connectu.api

import com.onelio.connectu.App

const val URL_LOGIN = "https://autentica.cpd.ua.es/cas/login?service=https%3a%2f%2fcvnet.cpd.ua.es%2fuacloud%2fhome%2findexVerificado"

class LoginRequest(app: App, r: LoginResponse) : Request() {

    interface LoginResponse {
        fun onCompletion()
    }

    init {
        Get(URL_LOGIN, object : UAResponse {
            override fun onError(code: Int) {
                app.setConnected(false)
                r.onCompletion()
            }
            override fun onResponse(body: String, data: String) {
                if (!containsLoginHost(data)) {
                    app.setConnected(true)
                } else {
                    app.setConnected(false)
                }
                r.onCompletion()
            }
        })
    }

}