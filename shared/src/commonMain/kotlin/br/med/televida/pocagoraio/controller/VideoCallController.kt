package br.med.televida.pocagoraio.controller


import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Define o contrato para um controlador de videochamadas.
 * Esta interface é agnóstica de plataforma e reside em commonMain.
 */
interface VideoCallController {

    val callState: StateFlow<CallState>
    val events: SharedFlow<CallEvent>

    fun initialize(config: AgoraConfig)
    fun join()
    fun leave()

    fun muteAudio(muted: Boolean)
    fun muteVideo(muted: Boolean)
}

/**
 * DECLARAÇÃO ESPERADA (EXPECT):
 * Esta é uma "promessa" de que cada plataforma (androidMain, iosMain)
 * irá fornecer uma implementação 'actual' para esta declaração.
 *
 * É a forma como o KMP conecta o código comum ao código específico da plataforma.
 */
expect fun createVideoCallController(
    context: Any, // 'Any' para ser genérico (será 'Context' no Android, 'UIApplication' no iOS, etc.)
    coroutineScope: CoroutineScope
): VideoCallController
