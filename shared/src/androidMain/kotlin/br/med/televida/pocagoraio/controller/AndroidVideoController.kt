package br.med.televida.pocagoraio.controller

import android.content.Context
import android.view.SurfaceView
import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler.RtcStats
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AndroidVideoController(
    private val context: Context,
    private val coroutineScope: CoroutineScope // Passar um CoroutineScope para gerenciar o ciclo de vida
) : VideoCallController {

    private var rtcEngine: RtcEngine? = null
    private lateinit var config: AgoraConfig

    // StateFlow para o estado atual da chamada (ex: Inativo, Em Chamada, etc.)
    private val _callState = MutableStateFlow<CallState>(CallState.Inactive)
    override val callState = _callState.asStateFlow()

    // SharedFlow para eventos pontuais (ex: Usuário entrou, Usuário saiu)
    private val _events = MutableSharedFlow<CallEvent>()
    override val events = _events.asSharedFlow()

    // Handler para receber eventos do motor do Agora
    private val eventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            // Sucesso ao entrar no canal, atualiza o estado
            _callState.value = CallState.Active
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            // Um usuário remoto entrou, emite o evento para a UI
            coroutineScope.launch {
                _events.emit(CallEvent.RemoteUserJoined(uid))
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            // Um usuário remoto saiu
            coroutineScope.launch {
                _events.emit(CallEvent.RemoteUserLeft(uid))
            }
        }

        override fun onLeaveChannel(stats: RtcStats) {
            // Sucesso ao sair do canal
            _callState.value = CallState.Inactive
        }

        override fun onError(err: Int) {
            // Trata erros do SDK
            coroutineScope.launch {
                _events.emit(CallEvent.Error("Agora Error: $err"))
            }
        }
    }

    override fun initialize(config: AgoraConfig) {
        this.config = config
        try {
            val rtcConfig = RtcEngineConfig().apply {
                mContext = context
                mAppId = config.appId
                mEventHandler = eventHandler
            }
            rtcEngine = RtcEngine.create(rtcConfig)
            rtcEngine?.enableVideo()
        } catch (e: Exception) {
            throw RuntimeException("Falha ao inicializar o RtcEngine: ${e.message}")
        }
    }

    override fun join() {
        if (rtcEngine == null) throw IllegalStateException("Controller não inicializado. Chame initialize() primeiro.")

        val options = ChannelMediaOptions().apply {
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        }

        rtcEngine?.joinChannel(config.token, config.channelName, config.uid, options)
    }

    override fun leave() {
        rtcEngine?.stopPreview()
        rtcEngine?.leaveChannel()
        RtcEngine.destroy() // Libera todos os recursos
        rtcEngine = null
    }

    override fun muteAudio(muted: Boolean) {
        rtcEngine?.muteLocalAudioStream(muted)
    }

    override fun muteVideo(muted: Boolean) {
        rtcEngine?.muteLocalVideoStream(muted)
    }

    // --- Funções Específicas da Plataforma (não estão na interface) ---

    fun setupLocalVideo(surfaceView: SurfaceView) {
        if (rtcEngine == null) return
        rtcEngine?.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config.uid))
        rtcEngine?.startPreview()
    }

    fun setupRemoteVideo(surfaceView: SurfaceView, remoteUid: Int) {
        if (rtcEngine == null) return
        rtcEngine?.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, remoteUid))
    }
}
