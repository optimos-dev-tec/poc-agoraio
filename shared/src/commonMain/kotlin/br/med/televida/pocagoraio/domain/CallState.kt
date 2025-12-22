package br.med.televida.pocagoraio.domain

sealed class CallState {
    object Idle : CallState()
    object Initializing : CallState()
    object Connecting : CallState()
    object Connected : CallState()
    object Reconnecting : CallState()
    object Ended : CallState()
    data class Error(val error: CallError) : CallState()
}