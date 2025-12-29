package br.med.televida.pocagoraio.controller

import android.content.Context
import android.util.Log
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

class AndroidVideoCallController(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : VideoCallController, IRtcEngineEventHandler() {

    private var rtcEngine: RtcEngine? = null
    private lateinit var config: AgoraConfig

    private val _callState = MutableStateFlow<CallState>(CallState.Inactive)
    override val callState = _callState.asStateFlow()

    private val _events = MutableSharedFlow<CallEvent>()
    override val events = _events.asSharedFlow()

    // ==========================
    // Agora Callbacks
    // ==========================

    override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
        Log.d("AGORA", "Entrou no canal: $channel uid=$uid")
        _callState.value = CallState.Connected

        coroutineScope.launch {
            _events.emit(CallEvent.LocalJoined)
        }
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        Log.d("AGORA", "Usuário remoto entrou: $uid")

        coroutineScope.launch {
            _events.emit(CallEvent.RemoteUserJoined(uid))
        }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        Log.d("AGORA", "Usuário remoto saiu: $uid")

        coroutineScope.launch {
            _events.emit(CallEvent.RemoteUserLeft(uid))
        }
    }

    override fun onLeaveChannel(stats: RtcStats) {
        Log.d("AGORA", "Saiu do canal")
        _callState.value = CallState.Ended
    }

    override fun onError(err: Int) {
        Log.e("AGORA", "Erro Agora: $err")

        coroutineScope.launch {
            _events.emit(CallEvent.Error("Agora error code: $err"))
        }
    }

    override fun onConnectionStateChanged(state: Int, reason: Int) {
        when (state) {
            Constants.CONNECTION_STATE_RECONNECTING -> {
                Log.w("AGORA", "Reconectando...")
                _callState.value = CallState.Reconnecting
            }
        }
    }

    // ==========================
    // Controller API
    // ==========================

    override fun initialize(config: AgoraConfig) {
        Log.d("AGORA", "initialize() chamado")

        this.config = config
        _callState.value = CallState.Initializing

        val rtcConfig = RtcEngineConfig().apply {
            mContext = context
            mAppId = config.appId
            mEventHandler = this@AndroidVideoCallController
        }

        rtcEngine = RtcEngine.create(rtcConfig)
        rtcEngine?.enableVideo()
    }

    override fun join() {
        if (rtcEngine == null) {
            throw IllegalStateException("RtcEngine não inicializado")
        }

        Log.d("AGORA", "join() channel=${config.channelName}")
        _callState.value = CallState.Connecting

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
        Log.d("AGORA", "leave()")
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
        Log.d("AGORA", "release()")
        RtcEngine.destroy()
        rtcEngine = null
        _callState.value = CallState.Inactive
    }

    // ==========================
    // Android-only helpers (UI)
    // ==========================

    fun setupLocalVideo(surfaceView: SurfaceView) {
        if (rtcEngine == null) return

        rtcEngine?.setupLocalVideo(
            VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0)
        )
        rtcEngine?.startPreview()
    }

    fun setupRemoteVideo(surfaceView: SurfaceView, remoteUid: Int) {
        if (rtcEngine == null) return

        rtcEngine?.setupRemoteVideo(
            VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, remoteUid)
        )
    }
}
