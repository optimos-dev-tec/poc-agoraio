package br.med.televida.pocagoraio.domain

sealed class CallEvent {
    object LocalJoined : CallEvent()
    data class RemoteJoined(val uid: Int) : CallEvent()
    data class RemoteLeft(val uid: Int) : CallEvent()
    object TokenExpired : CallEvent()
}