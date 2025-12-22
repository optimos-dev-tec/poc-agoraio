package br.med.televida.pocagoraio.viewmodel

import android.content.Context
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

/**
 * A implementação REAL (`actual`) do CallViewModel para a plataforma Android.
 *
 * Esta classe cumpre o contrato definido pela `expect class` em commonMain.
 * Seu construtor recebe um `Context` do Android, que é necessário para
 * inicializar o `AndroidVideoController`.
 */
actual class CallViewModel(context: Context) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val videoCallUseCase: VideoCallUseCase

    // Cumprindo as "promessas" da classe 'expect'
    actual val controller: VideoCallController get() = videoCallUseCase.controller
    actual  val callState: StateFlow<CallState> get() = videoCallUseCase.controller.callState
    actual val callEvents: SharedFlow<CallEvent> get() = videoCallUseCase.controller.events

    init {
        // O ViewModel constrói a árvore de dependências para a sua feature.
        val controller = createVideoCallController(context, viewModelScope)
        videoCallUseCase = VideoCallUseCase(controller)

        // Inicializa o UseCase com as configurações.
        // É uma boa prática mover o App ID para o build.gradle no futuro.
        val config = AgoraConfig(
            appId = "SEU_APP_ID_AQUI", // Seu App ID
            token = "SEU_TOKEN_AQUI", // Usar 'null' para testes sem um servidor de token
            channelName = "poc-channel-televida",
            uid = 0 // 0 deixa o Agora escolher um UID dinamicamente
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
