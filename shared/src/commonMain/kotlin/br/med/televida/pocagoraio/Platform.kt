package br.med.televida.pocagoraio

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform