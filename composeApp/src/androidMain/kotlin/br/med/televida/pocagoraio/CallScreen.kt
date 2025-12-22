package br.med.televida.pocagoraio

import android.view.SurfaceView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.med.televida.pocagoraio.controller.AndroidVideoController
import br.med.televida.pocagoraio.domain.CallEvent
import br.med.televida.pocagoraio.domain.CallState
import br.med.televida.pocagoraio.viewmodel.CallViewModel

@Composable
fun CallScreen() {
    val context = LocalContext.current

    // 1. Cria e lembra da instância do ViewModel.
    // O 'context' do Android é passado para a factory do controller.
    val viewModel = remember { CallViewModel(context) }

    // 2. Observa o estado da chamada (Inactive, Connecting, Connected).
    val callState by viewModel.callState.collectAsState()

    // 3. Cria uma lista reativa para armazenar os UIDs dos usuários remotos.
    val remoteUsers = remember { mutableStateListOf<Int>() }

    // 4. Efeito para coletar eventos pontuais (usuário entrou/saiu, erros).
    LaunchedEffect(viewModel) {
        viewModel.callEvents.collect { event ->
            when (event) {
                is CallEvent.RemoteUserJoined -> remoteUsers.add(event.uid)
                is CallEvent.RemoteUserLeft -> remoteUsers.remove(event.uid)
                is CallEvent.Error -> {
                    // Aqui você pode mostrar um Toast, Snackbar ou logar o erro
                    println("CallScreen Error: ${event.message}")
                }
                else -> Unit // Ignora outros eventos como LocalJoined, etc.
            }
        }
    }

    // 5. Efeito para limpar os recursos (chamar o release()) quando a tela sai da composição.
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onCleared()
        }
    }

    // 6. Desenha a UI com base no estado atual.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (callState) {
            CallState.Connecting -> {
                CircularProgressIndicator()
                Text("Conectando...", modifier = Modifier.padding(top = 80.dp))
            }
            CallState.Connected -> {
                // Tela da chamada ativa
                VideoCallContent(
                    viewModel = viewModel,
                    remoteUsers = remoteUsers
                )
            }
            else -> {
                // Tela inicial (inativa)
                InitialCallContent(
                    onJoinCall = { viewModel.joinCall() }
                )
            }
        }
    }
}

@Composable
private fun InitialCallContent(onJoinCall: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Pronto para iniciar a chamada", style = MaterialTheme.typography.titleMedium)
        Button(onClick = onJoinCall, modifier = Modifier.padding(top = 16.dp)) {
            Text("Entrar na Chamada")
        }
    }
}

@Composable
private fun VideoCallContent(
    viewModel: CallViewModel,
    remoteUsers: List<Int>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Área dos vídeos
        Column {
            // Vídeo local
            VideoView(
                modifier = Modifier.size(width = 150.dp, height = 200.dp),
                setupVideo = { surfaceView ->
                    (viewModel.controller as? AndroidVideoController)?.setupLocalVideo(surfaceView)
                }
            )

            // Vídeos remotos
            remoteUsers.forEach { remoteUid ->
                VideoView(
                    modifier = Modifier.size(width = 150.dp, height = 200.dp).padding(top = 8.dp),
                    setupVideo = { surfaceView ->
                        (viewModel.controller as? AndroidVideoController)?.setupRemoteVideo(surfaceView, remoteUid)
                    }
                )
            }
        }

        // Botões de controle
        Button(onClick = { viewModel.leaveCall() }) {
            Text("Sair da Chamada")
        }
    }
}

/**
 * Um Composable que encapsula o AndroidView para exibir o vídeo.
 * Ele usa uma 'key' para garantir que a view seja recriada se o ID do usuário mudar.
 */
@Composable
private fun VideoView(
    modifier: Modifier = Modifier,
    setupVideo: (SurfaceView) -> Unit
) {
    AndroidView(
        factory = { context ->
            SurfaceView(context).apply {
                // A configuração do vídeo será chamada no 'update'
            }
        },
        modifier = modifier,
        update = { surfaceView ->
            // Esta função é chamada para configurar ou atualizar a SurfaceView
            setupVideo(surfaceView)
        }
    )
}
