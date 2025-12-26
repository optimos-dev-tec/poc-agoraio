package br.med.televida.pocagoraio.controller

import kotlinx.coroutines.CoroutineScope

/**
 * IMPLEMENTAÇÃO REAL (ACTUAL) para a plataforma iOS:
 * Esta função cumpre a "promessa" feita pela 'expect fun' em commonMain,
 * criando e retornando uma instância do IosVideoController.
 */
actual fun createVideoCallController(
    context: Any,
    coroutineScope: CoroutineScope
): VideoCallController {
    // Agora que a classe existe, podemos retorná-la.
    return IosVideoController(coroutineScope)

}
