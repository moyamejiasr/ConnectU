package com.onelio.connectu.api

const val REQUEST_TIMEOUT = 408
const val UNAUTHORIZED = 401
const val RESPONSE_REDIRECT = 302
const val RESPONSE_OK = 200

open class Request {

    interface UAResponse {
        fun onResponse(body: String, data: String)
        fun onError(code: Int)
    }

    fun containsLoginHost(url: String?) : Boolean {
        return url!!.contains("autentica.cpd.ua.es")
    }

    fun isValid(code: Int): Boolean {
        return code == RESPONSE_OK
    }

    fun isRedirect(code: Int): Boolean {
        return code == RESPONSE_REDIRECT
    }

    protected fun Get(url: String, r: UAResponse) {
        khttp.async.get(URL_LOGIN, allowRedirects = false, onError = {
            r.onError(REQUEST_TIMEOUT)
        }, onResponse = {
            if (!isValid(statusCode)) {
                if (isRedirect(statusCode)) {
                    val location = headers["Location"]
                    if (containsLoginHost(location)) {
                        r.onError(UNAUTHORIZED)
                    } else {
                        if (location != null) {
                            Get(location, r)
                        } else {
                            r.onError(500)
                        }
                    }
                } else {
                    r.onError(statusCode)
                }
            } else {
                r.onResponse(text, url)
            }
        })
    }

}