package br.med.televida.pocagoraio

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import br.med.televida.pocagoraio.ui.CallScreen
import br.med.televida.pocagoraio.viewmodel.CallViewModel

@Composable
fun App() {
    val context = LocalContext.current

    // O ViewModel é criado aqui, onde o Context está disponível
    val viewModel = remember {
        CallViewModel(context)
    }

    MaterialTheme {
        CallScreen(viewModel = viewModel)
    }
}
