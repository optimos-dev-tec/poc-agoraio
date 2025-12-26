package br.med.televida.pocagoraio.controller

import br.med.televida.pocagoraio.config.AgoraConfig
import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import platform.darwin.NSObject // Importação base para delegados
// Importações do SDK do Agora para iOS
import agora.*

// 1. FAÇA A CLASSE HERDAR DE NSObject E DO DELEGADO DO AGORA
class IosVideoController(
    private val coroutineScope: CoroutineScope
) : NSObject(), VideoCallController, AgoraRtcEngineDelegateProtocol {

    private var rtcEngine: AgoraRtcEngineKit? = null
    private lateinit var config: AgoraConfig

    private val _callState = MutableStateFlow<CallState>(CallState.Inactive)
    override val callState = _callState.asStateFlow()

    private val _events = MutableSharedFlow<CallEvent>()
    override val events = _events.asSharedFlow()

    // --- Implementação da Interface VideoCallController ---

    override fun initialize(config: AgoraConfig) {
        this.config = config
        try {
            val engineConfig = AgoraRtcEngineConfig()
            engineConfig.appId = config.appId
            // 2. O DELEGADO AGORA É A PRÓPRIA CLASSE
            rtcEngine = AgoraRtcEngineKit.sharedEngineWithConfig(engineConfig, this)
            rtcEngine?.enableVideo()
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize AgoraRtcEngineKit: ${e.message}")
        }
    }

    override fun join() {
        if (rtcEngine == null) throw IllegalStateException("Controller not initialized.")
        _callState.value = CallState.Connecting

        val options = AgoraRtcChannelMediaOptions()
        options.channelProfile = AgoraChannelProfileCommunication
        options.clientRoleType = AgoraClientRoleBroadcaster

        // 3. A CHAMADA PARA ENTRAR NO CANAL É LIGEIRAMENTE DIFERENTE
        rtcEngine?.joinChannelByToken(
            config.token,
            config.channelName,
            null, // info (opcional)
            config.uid.toUInt()
        ) { _, uid, _ ->
            // Este é o callback de sucesso
            _callState.value = CallState.Connected
            coroutineScope.launch {
                _events.emit(CallEvent.LocalJoined)
            }
        }
    }

    override fun leave() {
        rtcEngine?.leaveChannel(null)
        _callState.value = CallState.Inactive
    }

    override fun muteAudio(muted: Boolean) {
        rtcEngine?.muteLocalAudioStream(muted)
    }

    override fun muteVideo(muted: Boolean) {
        rtcEngine?.muteLocalVideoStream(muted)
    }

    override fun release() {
        AgoraRtcEngineKit.destroy()
        rtcEngine = null
    }

    // --- Implementação dos Métodos do Delegado (AgoraRtcEngineDelegateProtocol) ---

    // 4. OS MÉTODOS DE CALLBACK DO DELEGADO PRECISAM SER SOBRESCRITOS
    override fun rtcEngine(engine: AgoraRtcEngineKit, didJoinedOfUid: ULong, elapsed: Int) {
        coroutineScope.launch {
            _events.emit(CallEvent.RemoteUserJoined(didJoinedOfUid.toInt()))
        }
    }

    override fun rtcEngine(engine: AgoraRtcEngineKit, didOfflineOfUid: ULong, reason: AgoraUserOfflineReason) {
        coroutineScope.launch {
            _events.emit(CallEvent.RemoteUserLeft(didOfflineOfUid.toInt()))
        }
    }

    override fun rtcEngine(engine: AgoraRtcEngineKit, didOccurError: AgoraErrorCode) {
        coroutineScope.launch {
            _events.emit(CallEvent.Error("Agora iOS Error: ${didOccurError.toInt()}"))
        }
    }

    // --- Funções Específicas do iOS (para a UI) ---

    // A UI do iOS precisará chamar estas funções para renderizar o vídeo
    fun setupLocalVideo(view: platform.UIKit.UIView) {
        val canvas = AgoraRtcVideoCanvas()
        canvas.uid = config.uid.toUInt()
        canvas.view = view
        canvas.renderMode = AgoraRenderModeHidden
        rtcEngine?.setupLocalVideo(canvas)
        rtcEngine?.startPreview()
    }

    fun setupRemoteVideo(view: platform.UIKit.UIView, remoteUid: Int) {
        val canvas = AgoraRtcVideoCanvas()
        canvas.uid = remoteUid.toUInt()
        canvas.view = view
        canvas.renderMode = AgoraRenderModeHidden
        rtcEngine?.setupRemoteVideo(canvas)
    }
}
