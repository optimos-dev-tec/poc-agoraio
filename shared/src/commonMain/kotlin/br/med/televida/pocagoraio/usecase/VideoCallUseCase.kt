package br.med.televida.pocagoraio.usecase

import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.controller.VideoCallController

class VideoCallUseCase(
    val controller: VideoCallController
) {

    fun initializeCall(config: AgoraConfig) {
        controller.initialize(config)
    }

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

    fun release() {
        controller.release()
    }
}
