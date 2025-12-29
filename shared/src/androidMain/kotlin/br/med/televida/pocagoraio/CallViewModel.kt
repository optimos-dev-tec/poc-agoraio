package br.med.televida.pocagoraio.viewmodel

import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import br.med.televida.pocagoraio.usecase.VideoCallUseCase
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class CallViewModel(
    private val useCase: VideoCallUseCase
) {

    /** Estado da chamada observado pela UI */
    val callState: StateFlow<CallState> = useCase.controller.callState

    /** Eventos pontuais observados pela UI */
    val callEvents: SharedFlow<CallEvent> = useCase.controller.events

    fun joinCall() {
        println("CallViewModel -> joinCall()")
        useCase.joinCall()
    }

    fun leaveCall() {
        println("CallViewModel -> leaveCall()")
        useCase.endCall()
    }

    fun toggleAudio(isMuted: Boolean) {
        useCase.muteAudio(isMuted)
    }

    fun toggleVideo(isMuted: Boolean) {
        useCase.muteVideo(isMuted)
    }

    fun onCleared() {
        println("CallViewModel -> onCleared()")
        useCase.release()
    }
}
