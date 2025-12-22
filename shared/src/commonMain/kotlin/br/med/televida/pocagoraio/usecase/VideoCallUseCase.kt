package br.med.televida.pocagoraio.usecase

import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.controller.VideoCallController

class VideoCallUseCase(
    // Torne o controller público para o ViewModel acessar os `Flows`
    val controller: VideoCallController
) {

    // Renomeado para ser mais descritivo
    fun initializeCall(config: AgoraConfig) {
        controller.initialize(config)
    }

    // Novo método para iniciar a tentativa de conexão
    fun joinCall() {
        controller.join()
    }

    fun endCall() {
        controller.leave()
    }

    fun muteAudio(muted: Boolean) {
        controller.muteAudio(muted)
    }

    fun muteVideo(muted: Boolean) {
        controller.muteVideo(muted)
    }

    // Novo método para liberar recursos
    fun release() {
        controller.release()
    }
}
