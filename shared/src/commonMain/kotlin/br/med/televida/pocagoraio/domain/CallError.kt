package br.med.televida.pocagoraio.domain

sealed class CallError {
    object InvalidToken : CallError()
    object NetworkFailure : CallError()
    object PermissionDenied : CallError()
    data class Unknown(val message: String?) : CallError()
}
