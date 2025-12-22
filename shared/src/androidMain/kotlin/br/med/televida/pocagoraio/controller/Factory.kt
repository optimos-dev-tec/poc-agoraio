package br.med.televida.pocagoraio.controller

import android.content.Context
import kotlinx.coroutines.CoroutineScope

/**
 * IMPLEMENTAÇÃO REAL (ACTUAL) para a plataforma Android:
 * Esta função cumpre a "promessa" feita pela 'expect fun' em commonMain.
 * Ela sabe como construir um AndroidVideoController, passando o Context do Android.
 */
actual fun createVideoCallController(
    context: Any,
    coroutineScope: CoroutineScope
): VideoCallController {
    // Converte o 'Any' genérico para o 'Context' específico do Android
    // e cria a instância real do controller.
    return AndroidVideoController(context as Context, coroutineScope)
}
    