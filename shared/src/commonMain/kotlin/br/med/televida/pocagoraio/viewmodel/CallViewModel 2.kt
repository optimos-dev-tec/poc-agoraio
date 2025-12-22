package br.med.televida.pocagoraio.viewmodel

import br.med.televida.pocagoraio.controller.VideoCallController
import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Define a "expectativa" para o CallViewModel no código comum.
 *
 * Esta classe `expect` declara a API pública que o ViewModel deve ter em todas as plataformas.
 * A implementação real (`actual`) será fornecida em cada source set específico (androidMain, iosMain, etc.).
 *
 * Isso permite que o código comum (como a UI em composeApp) programe contra uma abstração,
 * enquanto permite que cada plataforma injete suas próprias dependências (como o `Context` do Android).
 */
expect class CallViewModel {
    /**
     * Expõe o controller para a UI.
     * Necessário para que a UI (específica da plataforma) possa configurar as views de vídeo.
     */
    val controller: VideoCallController

    /**
     * Expõe o estado da chamada para a UI observar.
     */
    val callState: StateFlow<CallState>

    /**
     * Expõe os eventos pontuais da chamada para a UI observar.
     */
    val callEvents: SharedFlow<CallEvent>

    /**
     * Inicia o processo para entrar em um canal de chamada.
     */
    fun joinCall()

    /**
     * Inicia o processo para sair do canal de chamada.
     */
    fun leaveCall()

    /**
     * Alterna o estado de mudo do áudio local.
     */
    fun toggleAudio(isMuted: Boolean)

    /**
     * Alterna o estado de mudo do vídeo local.
     */
    fun toggleVideo(isMuted: Boolean)

    /**
     * Libera todos os recursos mantidos pelo ViewModel e suas dependências.
     * Deve ser chamado quando a UI que o utiliza é destruída.
     */
    fun onCleared()
}
