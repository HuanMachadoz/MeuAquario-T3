package br.edu.up.meuaquario.ui.telas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.up.meuaquario.dados.Aquario
import br.edu.up.meuaquario.dados.Especie
import br.edu.up.meuaquario.dados.Repositorio
import br.edu.up.meuaquario.navegacao.Rotas
import br.edu.up.meuaquario.ui.componentes.BarraOcupacao
import br.edu.up.meuaquario.ui.componentes.ChipRotulado
import br.edu.up.meuaquario.ui.componentes.TelaItemRemovido
import br.edu.up.meuaquario.ui.componentes.corDoStatus
import br.edu.up.meuaquario.ui.componentes.corDoTipo
import br.edu.up.meuaquario.ui.componentes.corSuaveDoStatus
import br.edu.up.meuaquario.ui.theme.AquaEscura
import br.edu.up.meuaquario.ui.theme.AquaPrimaria
import br.edu.up.meuaquario.ui.theme.Contorno
import br.edu.up.meuaquario.ui.theme.SuperficieVariante
import br.edu.up.meuaquario.ui.theme.TextoPrincipal
import br.edu.up.meuaquario.ui.theme.TextoSecundario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TelaDetalheAquario(navController: NavHostController, aquarioId: Int) {
    var aquario by remember { mutableStateOf<Aquario?>(null) }
    var especies by remember { mutableStateOf<List<Especie>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    fun recarregar() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val a = Repositorio.buscarAquarioPorId(aquarioId)
                val e = Repositorio.buscarEspeciesDoAquario(aquarioId)
                withContext(Dispatchers.Main) {
                    aquario = a
                    especies = e
                    carregando = false
                }
            } catch (ex: Exception) {
                android.util.Log.e("TelaDetalheAquario", "Erro ao carregar", ex)
                withContext(Dispatchers.Main) { carregando = false }
            }
        }
    }

    LaunchedEffect(aquarioId) { recarregar() }

    when {
        carregando -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Carregando...", color = TextoSecundario)
            }
        }
        aquario == null -> {
            TelaItemRemovido(aoVoltar = { navController.popBackStack() })
        }
        else -> {
            DetalheDoAquario(
                navController = navController,
                aquario = aquario!!,
                especies = especies,
                aoRemover = {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Repositorio.removerAquario(aquarioId)
                            withContext(Dispatchers.Main) { navController.popBackStack() }
                        } catch (ex: Exception) {
                            android.util.Log.e("TelaDetalheAquario", "Erro ao remover", ex)
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetalheDoAquario(
    navController: NavHostController,
    aquario: Aquario,
    especies: List<Especie>,
    aoRemover: () -> Unit
) {

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = aquario.nome,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = aoRemover) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remover aquário"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = corDoTipo(aquario.tipo),
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { espacamento ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CartaoIdentificacao(aquario)
            CartaoVolumeCalculado(aquario)
            CartaoOcupacao(aquario, especies)
            CartaoEspeciesDoAquario(
                especies = especies,
                aoAbrirEspecie = { idEspecie ->
                    navController.navigate(Rotas.detalheEspecie(idEspecie))
                }
            )
            Button(
                onClick = { navController.navigate(Rotas.novaEspecie(aquario.id)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Adicionar espécie neste aquário")
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CartaoIdentificacao(aquario: Aquario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = aquario.nome,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipal
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Medidas: ${aquario.comprimentoCm} × ${aquario.larguraCm} × " +
                        "${aquario.alturaCm} cm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
            ChipRotulado(
                texto = aquario.tipo.rotulo,
                corFundo = corDoTipo(aquario.tipo).copy(alpha = 0.14f),
                corTexto = corDoTipo(aquario.tipo)
            )
        }
    }
}

@Composable
private fun CartaoVolumeCalculado(aquario: Aquario) {
    val volume = Repositorio.volumeLitros(aquario)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Capacidade calculada",
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "${aquario.comprimentoCm} × ${aquario.larguraCm} × " +
                    "${aquario.alturaCm} ÷ 1000",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$volume",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AquaPrimaria
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "litros",
                    style = MaterialTheme.typography.titleMedium,
                    color = AquaPrimaria,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Este número não fica guardado em lugar nenhum: ele é recalculado " +
                    "a cada leitura a partir das medidas do aquário.",
                style = MaterialTheme.typography.labelSmall,
                color = TextoSecundario
            )
        }
    }
}

@Composable
private fun CartaoOcupacao(aquario: Aquario, especies: List<Especie>) {
    val volume = Repositorio.volumeLitros(aquario)
    val ocupados = Repositorio.litrosOcupados(especies)
    val percentual = Repositorio.percentualOcupacao(aquario, ocupados)
    val status = Repositorio.statusOcupacao(percentual)
    val restante = Repositorio.capacidadeRestante(aquario, ocupados)
    val cor = corDoStatus(status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ocupação",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal,
                    modifier = Modifier.weight(1f)
                )
                ChipRotulado(
                    texto = status.rotulo,
                    corFundo = corSuaveDoStatus(status),
                    corTexto = cor
                )
            }

            Spacer(Modifier.height(14.dp))

            BarraOcupacao(fracao = Repositorio.fracaoOcupacao(percentual), cor = cor)

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$ocupados L de $volume L",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoPrincipal,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$percentual%",
                    style = MaterialTheme.typography.titleMedium,
                    color = cor
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = status.descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )

            if (restante > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Ainda há cerca de $restante L livres neste aquário.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoSecundario
                )
            }
        }
    }
}

@Composable
private fun CartaoEspeciesDoAquario(
    especies: List<Especie>,
    aoAbrirEspecie: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                text = "Peixes neste aquário (${especies.size})",
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Espécies cadastradas na outra lista que escolheram este aquário.",
                style = MaterialTheme.typography.labelSmall,
                color = TextoSecundario
            )
            Spacer(Modifier.height(10.dp))

            if (especies.isEmpty()) {
                Text(
                    text = "Nenhuma espécie vive aqui ainda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            } else {
                especies.forEachIndexed { indice, especie ->
                    if (indice > 0) {
                        HorizontalDivider(color = SuperficieVariante)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { aoAbrirEspecie(especie.id) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SetMeal,
                            contentDescription = null,
                            tint = AquaPrimaria,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = especie.nome,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextoPrincipal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${especie.tamanhoAdultoCm} cm × ${especie.quantidade} " +
                                    "· pH ${especie.phIdeal}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextoSecundario
                            )
                        }
                        Text(
                            text = "${Repositorio.litrosNecessarios(especie)} L",
                            style = MaterialTheme.typography.labelSmall,
                            color = AquaEscura,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Contorno
                        )
                    }
                }
            }
        }
    }
}
