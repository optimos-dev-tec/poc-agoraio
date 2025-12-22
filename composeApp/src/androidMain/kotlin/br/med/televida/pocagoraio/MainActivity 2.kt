package br.med.televida.pocagoraio

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    // 1. Cria um launcher para pedir múltiplas permissões.
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                // Todas as permissões foram concedidas. Carrega o App.
                loadApp()
            } else {
                // Pelo menos uma permissão foi negada.
                // Aqui você pode mostrar uma mensagem de erro ou fechar o app.
                // Por enquanto, vamos apenas fechar.
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 2. Pede as permissões necessárias ao iniciar a activity.
        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }

    // 3. Função que carrega a UI do Compose DEPOIS que as permissões forem garantidas.
    private fun loadApp() {
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
