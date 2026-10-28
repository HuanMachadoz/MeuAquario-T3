package br.edu.up.meuaquario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.up.meuaquario.navegacao.AppMeuAquario
import br.edu.up.meuaquario.ui.theme.MeuAquarioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MeuAquarioTheme {
                AppMeuAquario()
            }
        }
    }
}
