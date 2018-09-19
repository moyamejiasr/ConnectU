package com.onelio.connectu.api

import khttp.responses.Response

const val RESPONSE_REDIRECT = 302
const val RESPONSE_OK = 200

const val LOGIN_HOST = "autentica.cpd.ua.es"

open class Request {

    protected fun isLoggedIn(r : Response) : Boolean {
        // Redirection to login page - Logged out
        if (r.statusCode == RESPONSE_REDIRECT && r.headers["Location"]!!.contains(LOGIN_HOST)) {
            return false
        } else {
            // Actually in login page - Not logged in
            if (r.url.contains(LOGIN_HOST)) {
                return false
            }
        }
        // Logged in
        return true
    }

}