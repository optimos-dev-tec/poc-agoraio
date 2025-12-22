package br.med.televida.pocagoraio.controller

import kotlinx.coroutines.CoroutineScope

/**
 * IMPLEMENTAÇÃO REAL (ACTUAL) para a plataforma iOS:
 * Esta função cumpre a "promessa" da 'expect fun' de commonMain.
 *
 * Como ainda não implementamos a lógica para iOS, vamos lançar um erro
 * para garantir que o app quebre se tentarmos usar isso antes de estar pronto.
 */
actual fun createVideoCallController(
    context: Any,
    coroutineScope: CoroutineScope
): VideoCallController {
    // TODO: Implementar o IosVideoController usando o SDK do Agora para iOS.
    // Por enquanto, esta implementação vazia satisfaz o compilador.
    throw NotImplementedError("VideoCallController não foi implementado para iOS ainda.")
}
