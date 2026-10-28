package br.edu.up.meuaquario.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val EsquemaDeCores = lightColorScheme(
    primary = AquaPrimaria,
    onPrimary = Color.White,
    primaryContainer = AquaClara,
    onPrimaryContainer = AquaEscura,

    secondary = AquaSecundaria,
    onSecondary = Color.White,
    secondaryContainer = AquaClaraSuave,
    onSecondaryContainer = AquaEscura,

    tertiary = AquaTerciaria,
    onTertiary = Color.White,

    background = FundoApp,
    onBackground = TextoPrincipal,
    surface = SuperficieApp,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = TextoSecundario,
    outline = Contorno,

    error = VermelhoAlerta,
    onError = Color.White,
    errorContainer = VermelhoSuave,
    onErrorContainer = VermelhoAlerta
)

private val TipografiaApp = Typography(
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
)

@Composable
fun MeuAquarioTheme(conteudo: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaDeCores,
        typography = TipografiaApp,
        content = conteudo
    )
}
