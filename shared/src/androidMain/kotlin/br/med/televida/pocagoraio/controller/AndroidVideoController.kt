package br.med.televida.pocagoraio.controller

import android.content.Context
import android.view.SurfaceView
import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AndroidVideoController(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : VideoCallController, IRtcEngineEventHandler() {

    private var rtcEngine: RtcEngine? = null
    private lateinit var config: AgoraConfig

    private val _callState = MutableStateFlow<CallState>(CallState.Inactive)
    override val callState = _callState.asStateFlow()

    private val _events = MutableSharedFlow<CallEvent>()
    override val events = _events.asSharedFlow()

    override fun initialize(config: AgoraConfig) {
        this.config = config

        coroutineScope.launch {
            _callState.emit(CallState.Initializing)
        }

        val rtcConfig = RtcEngineConfig().apply {
            mContext = context
            mAppId = config.appId
            mEventHandler = this@AndroidVideoController
        }

        rtcEngine = RtcEngine.create(rtcConfig)

        rtcEngine?.apply {
            enableVideo()
            enableAudio()
        }
    }

    override fun join() {
        if (rtcEngine == null) {
            throw IllegalStateException("Controller não inicializado")
        }

        coroutineScope.launch {
            _callState.emit(CallState.Connecting)
        }

        val options = ChannelMediaOptions().apply {
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        }

        rtcEngine?.joinChannel(
            config.token,
            config.channelName,
            config.uid,
            options
        )
    }

    override fun leave() {
        rtcEngine?.stopPreview()
        rtcEngine?.leaveChannel()
    }

    override fun muteAudio(muted: Boolean) {
        rtcEngine?.muteLocalAudioStream(muted)
    }

    override fun muteVideo(muted: Boolean) {
        rtcEngine?.muteLocalVideoStream(muted)
    }

    override fun release() {
        rtcEngine?.stopPreview()
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
    }

    /* ==================== AGORA CALLBACKS ==================== */

    override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
        coroutineScope.launch {
            _callState.emit(CallState.Connected)
            _events.emit(CallEvent.LocalJoined)
        }
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        coroutineScope.launch {
            _events.emit(CallEvent.RemoteUserJoined(uid))
        }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        coroutineScope.launch {
            _events.emit(CallEvent.RemoteUserLeft(uid))
        }
    }

    override fun onLeaveChannel(stats: RtcStats) {
        coroutineScope.launch {
            _callState.emit(CallState.Ended)
        }
    }

    override fun onError(err: Int) {
        coroutineScope.launch {
            _events.emit(CallEvent.Error("Agora error code: $err"))
        }
    }

    /* ==================== VIDEO SETUP ==================== */

    fun setupLocalVideo(surfaceView: SurfaceView) {
        rtcEngine?.setupLocalVideo(
            VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0)
        )
        rtcEngine?.startPreview()
    }

    fun setupRemoteVideo(surfaceView: SurfaceView, remoteUid: Int) {
        rtcEngine?.setupRemoteVideo(
            VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, remoteUid)
        )
    }
}
