package br.med.televida.pocagoraio.controller

import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface VideoCallController {

    /** Estado atual da chamada */
    val callState: StateFlow<CallState>

    /** Eventos pontuais (usuário entrou, erro, token expirado, etc.) */
    val events: SharedFlow<CallEvent>

    /** Inicializa o SDK com as configurações necessárias */
    fun initialize(config: AgoraConfig)

    /** Inicia a conexão no canal */
    fun join()

    /** Encerra a chamada */
    fun leave()

    fun muteAudio(muted: Boolean)
    fun muteVideo(muted: Boolean)

    /** Libera completamente os recursos */
    fun release()
}

expect fun createVideoCallController(
    context: Any,
    coroutineScope: CoroutineScope
): VideoCallController
