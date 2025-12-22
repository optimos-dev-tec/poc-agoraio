package br.med.televida.pocagoraio.viewmodel

import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.controller.VideoCallController
import br.med.televida.pocagoraio.controller.createVideoCallController
import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import br.med.televida.pocagoraio.usecase.VideoCallUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

// A implementação REAL (`actual`) do CallViewModel para a plataforma iOS.
actual class CallViewModel {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val videoCallUseCase: VideoCallUseCase

    // O construtor é vazio, pois não precisa de dependências externas como o `Context`
    init {
        val controller = createVideoCallController(Unit, viewModelScope) // Passamos `Unit` como "contexto"
        videoCallUseCase = VideoCallUseCase(controller)

        val config = AgoraConfig(
            appId = "9fd3424ea7f34bcfb2b312a12cdf69d7", // Use o mesmo App ID
            token = "SEU_TOKEN_AQUI",
            channelName = "poc-channel-televida",
            uid = 0
        )
        videoCallUseCase.initializeCall(config)
    }

    actual val controller: VideoCallController get() = videoCallUseCase.controller
    actual val callState: StateFlow<CallState> get() = videoCallUseCase.controller.callState
    actual val callEvents: SharedFlow<CallEvent> get() = videoCallUseCase.controller.events

    actual fun joinCall() { videoCallUseCase.joinCall() }
    actual fun leaveCall() { videoCallUseCase.endCall() }
    actual fun toggleAudio(isMuted: Boolean) { videoCallUseCase.muteAudio(isMuted) }
    actual fun toggleVideo(isMuted: Boolean) { videoCallUseCase.muteVideo(isMuted) }

    actual fun onCleared() {
        viewModelScope.cancel()
        videoCallUseCase.release()
    }
}
