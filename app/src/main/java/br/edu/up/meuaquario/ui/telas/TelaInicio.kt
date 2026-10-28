package br.edu.up.meuaquario.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.up.meuaquario.dados.Aquario
import br.edu.up.meuaquario.dados.Especie
import br.edu.up.meuaquario.dados.Repositorio
import br.edu.up.meuaquario.navegacao.Rotas
import br.edu.up.meuaquario.ui.componentes.CabecalhoSecao
import br.edu.up.meuaquario.ui.componentes.CartaoEstatistica
import br.edu.up.meuaquario.ui.componentes.lembrarGatilhoDeRecarga
import br.edu.up.meuaquario.ui.theme.AquaPrimaria
import br.edu.up.meuaquario.ui.theme.AquaSecundaria
import br.edu.up.meuaquario.ui.theme.AquaTerciaria
import br.edu.up.meuaquario.ui.theme.GradienteFim
import br.edu.up.meuaquario.ui.theme.GradienteInicio
import br.edu.up.meuaquario.ui.theme.TextoSecundario
import br.edu.up.meuaquario.ui.theme.VerdeOk
import br.edu.up.meuaquario.ui.theme.VerdeSuave
import br.edu.up.meuaquario.ui.theme.VermelhoAlerta
import br.edu.up.meuaquario.ui.theme.VermelhoSuave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaInicio(navController: NavHostController) {

    val gatilho = lembrarGatilhoDeRecarga()
    var aquarios by remember { mutableStateOf<List<Aquario>>(emptyList()) }
    var especies by remember { mutableStateOf<List<Especie>>(emptyList()) }

    LaunchedEffect(gatilho) {
        try {
            val a = Repositorio.buscarAquarios()
            val e = Repositorio.buscarEspecies()
            withContext(Dispatchers.Main) {
                aquarios = a
                especies = e
            }
        } catch (ex: Exception) {
            android.util.Log.e("TelaInicio", "Erro ao carregar dados", ex)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("MeuAquário", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AquaPrimaria,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { espacamento ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .verticalScroll(rememberScrollState())
        ) {
            CabecalhoDegrade(aquarios, especies)

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(Modifier.height(16.dp))
                LinhaDeEstatisticas(aquarios, especies)

                CabecalhoSecao("Situação dos aquários")
                CartaoSituacao(navController, aquarios, especies)

                CabecalhoSecao("Atalhos")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BotaoAtalho(
                        texto = "Ver aquários",
                        icone = Icons.Filled.WaterDrop,
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate(Rotas.AQUARIOS) }

                    BotaoAtalho(
                        texto = "Ver espécies",
                        icone = Icons.Filled.SetMeal,
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate(Rotas.ESPECIES) }
                }

                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun CabecalhoDegrade(aquarios: List<Aquario>, especies: List<Especie>) {
    val volumeTotal = Repositorio.volumeTotal(aquarios)
    val ocupados = Repositorio.litrosOcupados(especies)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Brush.linearGradient(listOf(GradienteInicio, GradienteFim)))
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Column {
            Text(
                text = "Seus aquários hoje",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$volumeTotal",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "litros cadastrados",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.WaterDrop,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "$ocupados L já ocupados pelos peixes",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun LinhaDeEstatisticas(aquarios: List<Aquario>, especies: List<Especie>) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CartaoEstatistica(
            icone = Icons.Filled.WaterDrop,
            valor = "${aquarios.size}",
            rotulo = "aquários",
            cor = AquaSecundaria,
            modifier = Modifier.weight(1f)
        )
        CartaoEstatistica(
            icone = Icons.Filled.SetMeal,
            valor = "${Repositorio.totalDePeixes(especies)}",
            rotulo = "peixes",
            cor = AquaTerciaria,
            modifier = Modifier.weight(1f)
        )
        CartaoEstatistica(
            icone = Icons.Filled.Waves,
            valor = "${Repositorio.litrosOcupados(especies)} L",
            rotulo = "ocupados",
            cor = VerdeOk,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CartaoSituacao(
    navController: NavHostController,
    aquarios: List<Aquario>,
    especies: List<Especie>
) {
    val comProblema = Repositorio.aquariosSuperlotados(aquarios, especies)
    val totalDeAquarios = aquarios.size
    val tudoCerto = comProblema.isEmpty()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tudoCerto) VerdeSuave else VermelhoSuave
        )
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (tudoCerto) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    contentDescription = null,
                    tint = if (tudoCerto) VerdeOk else VermelhoAlerta
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = if (tudoCerto) {
                        "Tudo em ordem"
                    } else {
                        "${comProblema.size} aquário(s) superlotado(s)"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = if (tudoCerto) VerdeOk else VermelhoAlerta,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = when {
                    totalDeAquarios == 0 ->
                        "Você ainda não cadastrou nenhum aquário. Use o atalho abaixo para começar."
                    tudoCerto ->
                        "Nenhum aquário passou do volume recomendado para os peixes que vivem nele."
                    else ->
                        "Toque em um dos aquários abaixo para ver a ocupação e ajustar a quantidade de peixes."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )

            comProblema.forEach { aquario ->
                TextButton(onClick = { navController.navigate(Rotas.detalheAquario(aquario.id)) }) {
                    Text("Ver ${aquario.nome} →", color = VermelhoAlerta)
                }
            }
        }
    }
}

@Composable
private fun BotaoAtalho(
    texto: String,
    icone: ImageVector,
    modifier: Modifier = Modifier,
    aoClicar: () -> Unit
) {
    OutlinedButton(
        onClick = aoClicar,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Icon(icone, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}
