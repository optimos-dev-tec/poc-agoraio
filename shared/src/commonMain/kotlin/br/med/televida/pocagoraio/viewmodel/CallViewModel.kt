package br.med.televida.pocagoraio.viewmodel

import br.med.televida.pocagoraio.controller.VideoCallController
import br.med.televida.pocagoraio.domain.CallState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CallViewModel(
    val controller: VideoCallController
) {

    private val _callState = MutableStateFlow<CallState>(CallState.Inactive)
    val callState: StateFlow<CallState> = _callState

    fun joinCall() {
        println("CallViewModel -> joinCall()")

        _callState.value = CallState.Initializing

        controller.initialize()

        _callState.value = CallState.Connecting

        controller.joinChannel(
            token = null,
            channelName = "test_channel",
            uid = 0
        )
    }

    fun leaveCall() {
        println("CallViewModel -> leaveCall()")

        controller.leaveChannel()
        controller.release()

        _callState.value = CallState.Ended
    }

    fun toggleAudio(mute: Boolean) {
        controller.muteAudio(mute)
    }

    fun toggleVideo(mute: Boolean) {
        controller.muteVideo(mute)
    }

    fun onConnected() {
        println("CallViewModel -> Connected")
        _callState.value = CallState.Connected
    }

    fun onDisconnected() {
        println("CallViewModel -> Disconnected")
        _callState.value = CallState.Ended
    }
}
