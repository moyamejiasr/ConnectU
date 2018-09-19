package com.onelio.connectu.api

import com.onelio.connectu.App
import khttp.get
import khttp.structures.cookie.Cookie

const val URL_LOGIN = "https://autentica.cpd.ua.es/cas/login?service=https%3a%2f%2fcvnet.cpd.ua.es%2fuacloud%2fhome%2findexVerificado"

class LoginRequest(app: App) : Request() {



}