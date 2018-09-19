package com.onelio.connectu.types

class Account {
    /**
     * String composed of user email. Must be @alu.ua.es and
     * never encoded.
     */
    var Email = String()
    /**
     * String composed of user password. Must never be shared.
     */
    var Password = String()
    /**
     * Map of Key,Value valid Cookies to be used on connection.
     * Must at least be always initialized empty.
     */
    var Cookies : Map<String, String> = emptyMap()

    fun hasCookies() : Boolean {
        return Cookies.isNotEmpty()
    }
}