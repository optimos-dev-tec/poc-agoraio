package br.med.televida.pocagoraio.controller

import android.content.Context
import android.view.SurfaceView
import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
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
    private val coroutineScope: CoroutineScope
) : VideoCallController, IRtcEngineEventHandler() { // A herança foi adicionada aqui

    private var rtcEngine: RtcEngine? = null
    private lateinit var config: AgoraConfig

    // StateFlow para o estado atual da chamada
    private val _callState = MutableStateFlow<CallState>(CallState.Inactive)
    override val callState = _callState.asStateFlow()

    // SharedFlow para eventos pontuais
    private val _events = MutableSharedFlow<CallEvent>()
    override val events = _events.asSharedFlow()

    // Os métodos de evento agora são implementados diretamente pela classe.

    override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
        // Sucesso ao entrar no canal, atualiza o estado
        _callState.value = CallState.Connected
        coroutineScope.launch {
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
        // Sucesso ao sair do canal
        _callState.value = CallState.Inactive
    }

    override fun onError(err: Int) {
        coroutineScope.launch {
            _events.emit(CallEvent.Error("Agora Error: $err"))
        }
    }

    override fun initialize(config: AgoraConfig) {
        this.config = config

        val rtcConfig = RtcEngineConfig().apply {
            mContext = context
            mAppId = config.appId
            mEventHandler = this@AndroidVideoController
        }

        try {
            rtcEngine = RtcEngine.create(rtcConfig)

            // 3. Habilite o módulo de vídeo
            rtcEngine?.enableVideo()

        } catch (e: Exception) {
            // Se a criação falhar (ex: licença inválida, etc.), lance um erro claro
            throw RuntimeException("Falha ao inicializar o RtcEngine: ${e.message}")
        }
    }

    override fun join() {
        if (rtcEngine == null) throw IllegalStateException("Controller não inicializado. Chame initialize() primeiro.")

        // Antes de entrar, mude o estado para 'Connecting' para a UI poder mostrar um loading
        _callState.value = CallState.Connecting

        val options = ChannelMediaOptions().apply {
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        }
        rtcEngine?.joinChannel(config.token, config.channelName, config.uid, options)
    }

    override fun leave() {
        rtcEngine?.stopPreview()
        rtcEngine?.leaveChannel()
        // A chamada a destroy() é assíncrona, o estado mudará no callback onLeaveChannel
    }

    override fun muteAudio(muted: Boolean) {
        rtcEngine?.muteLocalAudioStream(muted)
    }

    override fun muteVideo(muted: Boolean) {
        rtcEngine?.muteLocalVideoStream(muted)
    }

    // As funções abaixo não fazem parte da interface e são específicas para a View do Android

    fun setupLocalVideo(surfaceView: SurfaceView) {
        if (rtcEngine == null) return
        rtcEngine?.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config.uid))
        rtcEngine?.startPreview()
    }

    fun setupRemoteVideo(surfaceView: SurfaceView, remoteUid: Int) {
        if (rtcEngine == null) return
        rtcEngine?.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, remoteUid))
    }

    override fun release() {
        // Função para limpar todos os recursos
        RtcEngine.destroy()
        rtcEngine = null
    }
}
