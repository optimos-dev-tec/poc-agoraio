package br.med.televida.pocagoraio.ui

import android.view.SurfaceView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.med.televida.pocagoraio.controller.AndroidVideoController
import br.med.televida.pocagoraio.domain.CallState
import br.med.televida.pocagoraio.viewmodel.CallViewModel

@Composable
fun CallScreen(
    viewModel: CallViewModel
) {
    val callState by viewModel.callState.collectAsState()

    // Lista simples de usuários remotos (pode evoluir depois)
    val remoteUsers = remember { mutableStateListOf<Int>() }

    Box(modifier = Modifier.fillMaxSize()) {
        when (callState) {

            CallState.Inactive -> {
                InitialCallContent(
                    onJoinCall = { viewModel.joinCall() }
                )
            }

            CallState.Initializing,
            CallState.Connecting,
            CallState.Reconnecting -> {
                LoadingCallContent()
            }

            CallState.Connected -> {
                VideoCallContent(
                    viewModel = viewModel,
                    remoteUsers = remoteUsers
                )
            }

            CallState.Ended -> {
                InitialCallContent(
                    onJoinCall = { viewModel.joinCall() }
                )
            }
        }
    }
}

@Composable
private fun InitialCallContent(
    onJoinCall: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onJoinCall) {
            Text("Entrar na Chamada")
        }
    }
}

@Composable
private fun LoadingCallContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Conectando...")
    }
}

@Composable
private fun VideoCallContent(
    viewModel: CallViewModel,
    remoteUsers: List<Int>
) {
    val context = LocalContext.current

    val controller = viewModel.controller as AndroidVideoController

    Box(modifier = Modifier.fillMaxSize()) {

        // Vídeo local
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = {
                SurfaceView(context).apply {
                    controller.setupLocalVideo(this)
                }
            }
        )

        // Controles
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(onClick = { viewModel.toggleAudio(true) }) {
                Text("Mutar Áudio")
            }

            Button(onClick = { viewModel.toggleVideo(true) }) {
                Text("Mutar Vídeo")
            }

            Button(onClick = { viewModel.leaveCall() }) {
                Text("Sair")
            }
        }
    }
}
