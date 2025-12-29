package br.med.televida.pocagoraio.controller

import android.content.Context
import kotlinx.coroutines.CoroutineScope

actual fun createVideoCallController(
    context: Any,
    coroutineScope: CoroutineScope
): VideoCallController {
    return AndroidVideoCallController(
        context = context as Context,
        coroutineScope = coroutineScope
    )
}
