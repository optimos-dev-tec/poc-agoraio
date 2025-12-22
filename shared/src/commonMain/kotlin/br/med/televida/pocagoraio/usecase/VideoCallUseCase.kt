package br.med.televida.pocagoraio.usecase

import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.controller.VideoCallController

class VideoCallUseCase(
    private val controller: VideoCallController
) {

    fun startCall(config: AgoraConfig) {
        controller.initialize(config)
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
}