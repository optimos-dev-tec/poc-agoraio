package br.med.televida.pocagoraio.viewmodel

import android.content.Context
import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.controller.VideoCallController
import br.med.televida.pocagoraio.controller.createVideoCallController
import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import br.med.televida.pocagoraio.usecase.VideoCallUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

actual class CallViewModel(context: Context) {

    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val videoCallUseCase: VideoCallUseCase

    actual val controller: VideoCallController
        get() = videoCallUseCase.controller

    actual val callState: StateFlow<CallState>
        get() = controller.callState

    actual val callEvents: SharedFlow<CallEvent>
        get() = controller.events

    init {
        val controller = createVideoCallController(context, viewModelScope)
        videoCallUseCase = VideoCallUseCase(controller)

        val config = AgoraConfig(
            appId = "SEU_APP_ID_REAL",
            channelName = "poc-channel-televida",
            token = null, // correto para testes
            uid = 0
        )

        videoCallUseCase.initializeCall(config)
    }

    actual fun joinCall() {
        videoCallUseCase.joinCall()
    }

    actual fun leaveCall() {
        videoCallUseCase.endCall()
    }

    actual fun toggleAudio(isMuted: Boolean) {
        videoCallUseCase.muteAudio(isMuted)
    }

    actual fun toggleVideo(isMuted: Boolean) {
        videoCallUseCase.muteVideo(isMuted)
    }

    actual fun onCleared() {
        viewModelScope.cancel()
        videoCallUseCase.release()
    }
}
