package br.med.televida.pocagoraio

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Agora, o App() é responsável apenas por configurar o tema
        // e chamar a tela principal da nossa POC.
        CallScreen()
    }
}
