package br.med.televida.pocagoraio.controller

import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementação vazia (placeholder) do VideoCallController para iOS.
 * O objetivo desta classe é apenas satisfazer o compilador KMP
 * para que o lado Android possa ser desenvolvido sem erros.
 *
 * TODO: Implementar a lógica real usando o SDK do Agora para iOS.
 */
class IosVideoController : VideoCallController {
    override val callState = MutableStateFlow<CallState>(CallState.Inactive).asStateFlow()
    override val events = MutableSharedFlow<CallEvent>().asSharedFlow()

    override fun initialize(config: AgoraConfig) {
        // Lógica de inicialização para iOS virá aqui
    }

    override fun join() {
        // Lógica para entrar em um canal no iOS
    }

    override fun leave() {
        // Lógica para sair de um canal no iOS
    }

    override fun muteAudio(muted: Boolean) {
        // Lógica para mutar áudio no iOS
    }

    override fun muteVideo(muted: Boolean) {
        // Lógica para mutar vídeo no iOS
    }

    override fun release() {
        // Lógica para liberar recursos no iOS
    }
}
