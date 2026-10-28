package br.edu.up.meuaquario.ui.telas

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.up.meuaquario.dados.Aquario
import br.edu.up.meuaquario.dados.Especie
import br.edu.up.meuaquario.dados.Repositorio
import br.edu.up.meuaquario.navegacao.Rotas
import br.edu.up.meuaquario.ui.componentes.BarraOcupacao
import br.edu.up.meuaquario.ui.componentes.ChipRotulado
import br.edu.up.meuaquario.ui.componentes.LinhaInfo
import br.edu.up.meuaquario.ui.componentes.TelaItemRemovido
import br.edu.up.meuaquario.ui.componentes.corDoStatus
import br.edu.up.meuaquario.ui.componentes.corSuaveDoStatus
import br.edu.up.meuaquario.ui.theme.AquaClaraSuave
import br.edu.up.meuaquario.ui.theme.AquaEscura
import br.edu.up.meuaquario.ui.theme.AquaPrimaria
import br.edu.up.meuaquario.ui.theme.AquaSecundaria
import br.edu.up.meuaquario.ui.theme.SuperficieVariante
import br.edu.up.meuaquario.ui.theme.TextoPrincipal
import br.edu.up.meuaquario.ui.theme.TextoSecundario
import br.edu.up.meuaquario.ui.theme.VermelhoAlerta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TelaDetalheEspecie(navController: NavHostController, especieId: Int) {
    var especie by remember { mutableStateOf<Especie?>(null) }
    var aquario by remember { mutableStateOf<Aquario?>(null) }
    var especiesDoAquario by remember { mutableStateOf<List<Especie>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    fun recarregar() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val esp = Repositorio.buscarEspeciePorId(especieId)
                val aq = esp?.let { Repositorio.buscarAquarioPorId(it.aquarioId) }
                val lista = esp?.let { Repositorio.buscarEspeciesDoAquario(it.aquarioId) } ?: emptyList()
                withContext(Dispatchers.Main) {
                    especie = esp
                    aquario = aq
                    especiesDoAquario = lista
                    carregando = false
                }
            } catch (ex: Exception) {
                android.util.Log.e("TelaDetalheEspecie", "Erro ao carregar", ex)
                withContext(Dispatchers.Main) { carregando = false }
            }
        }
    }

    LaunchedEffect(especieId) { recarregar() }

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
        especie == null -> {
            TelaItemRemovido(aoVoltar = { navController.popBackStack() })
        }
        else -> {
            DetalheDaEspecie(
                navController = navController,
                especie = especie!!,
                aquario = aquario,
                especiesDoAquario = especiesDoAquario,
                aoAlterarQuantidade = { nova ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Repositorio.alterarQuantidade(especieId, nova)
                            recarregar()
                        } catch (ex: Exception) {
                            android.util.Log.e("TelaDetalheEspecie", "Erro ao alterar quantidade", ex)
                        }
                    }
                },
                aoRemover = {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Repositorio.removerEspecie(especieId)
                            withContext(Dispatchers.Main) { navController.popBackStack() }
                        } catch (ex: Exception) {
                            android.util.Log.e("TelaDetalheEspecie", "Erro ao remover", ex)
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetalheDaEspecie(
    navController: NavHostController,
    especie: Especie,
    aquario: Aquario?,
    especiesDoAquario: List<Especie>,
    aoAlterarQuantidade: (Int) -> Unit,
    aoRemover: () -> Unit
) {

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = especie.nome, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                            contentDescription = "Remover espécie"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AquaPrimaria,
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
            CartaoIdentificacaoEspecie(especie)
            CartaoLitrosNecessarios(especie)
            CartaoAjusteQuantidade(
                especie = especie,
                aquario = aquario,
                especiesDoAquario = especiesDoAquario,
                aoAlterarQuantidade = aoAlterarQuantidade
            )
            CartaoOndeVive(
                aquario = aquario,
                especiesDoAquario = especiesDoAquario,
                aoAbrirAquario = {
                    if (aquario != null) navController.navigate(Rotas.detalheAquario(aquario.id))
                }
            )

            OutlinedButton(
                onClick = aoRemover,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    tint = VermelhoAlerta,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Remover esta espécie", color = VermelhoAlerta)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CartaoIdentificacaoEspecie(especie: Especie) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = especie.nome,
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipal
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = especie.nomeCientifico,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                fontStyle = FontStyle.Italic
            )
            Spacer(Modifier.height(12.dp))
            Row {
                ChipRotulado("×${especie.quantidade}", AquaClaraSuave, AquaEscura)
                Spacer(Modifier.width(6.dp))
                ChipRotulado("${especie.tamanhoAdultoCm} cm adulto", SuperficieVariante, TextoSecundario)
                Spacer(Modifier.width(6.dp))
                ChipRotulado("pH ${especie.phIdeal}", SuperficieVariante, TextoSecundario)
            }
        }
    }
}

@Composable
private fun CartaoLitrosNecessarios(especie: Especie) {
    val litros = Repositorio.litrosNecessarios(especie)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Carga que esta espécie representa",
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "${especie.tamanhoAdultoCm} cm × ${especie.quantidade} peixes",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$litros",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AquaPrimaria
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "litros necessários",
                    style = MaterialTheme.typography.titleMedium,
                    color = AquaPrimaria,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Regra usada por aquaristas: 1 litro de água por centímetro de peixe adulto.",
                style = MaterialTheme.typography.labelSmall,
                color = TextoSecundario
            )
        }
    }
}

@Composable
private fun CartaoAjusteQuantidade(
    especie: Especie,
    aquario: Aquario?,
    especiesDoAquario: List<Especie>,
    aoAlterarQuantidade: (Int) -> Unit
) {
    val ocupados = Repositorio.litrosOcupados(especiesDoAquario)
    val volume = aquario?.let { Repositorio.volumeLitros(it) } ?: 0
    val percentual = if (volume > 0) (ocupados * 100) / volume else 0
    val estourou = volume > 0 && ocupados > volume

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                text = "Ajustar quantidade",
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Altere aqui e veja os litros necessários e a ocupação do aquário " +
                    "mudarem na hora.",
                style = MaterialTheme.typography.labelSmall,
                color = TextoSecundario
            )

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { aoAlterarQuantidade(especie.quantidade - 1) },
                    enabled = especie.quantidade > 1
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Diminuir quantidade",
                        tint = if (especie.quantidade > 1) AquaPrimaria else SuperficieVariante
                    )
                }

                Text(
                    text = "${especie.quantidade}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextoPrincipal,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = { aoAlterarQuantidade(especie.quantidade + 1) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Aumentar quantidade",
                        tint = AquaPrimaria
                    )
                }

                Spacer(Modifier.weight(1f))

                ChipRotulado(
                    texto = "${Repositorio.litrosNecessarios(especie)} L",
                    corFundo = AquaClaraSuave,
                    corTexto = AquaEscura
                )
            }

            if (aquario != null) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Efeito no aquário “${aquario.nome}”:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
                Spacer(Modifier.height(8.dp))
                BarraOcupacao(
                    fracao = (percentual / 100f).coerceIn(0f, 1f),
                    cor = if (estourou) VermelhoAlerta else AquaSecundaria
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "$ocupados L ocupados de $volume L · $percentual%",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoSecundario
                )

                if (estourou) {
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = VermelhoAlerta,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Com esta quantidade o aquário passou do volume recomendado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = VermelhoAlerta
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoOndeVive(
    aquario: Aquario?,
    especiesDoAquario: List<Especie>,
    aoAbrirAquario: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Onde vive",
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal
            )
            Spacer(Modifier.height(6.dp))

            if (aquario == null) {
                Text(
                    text = "O aquário desta espécie não existe mais.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            } else {
                val ocupados = Repositorio.litrosOcupados(especiesDoAquario)
                val percentual = Repositorio.percentualOcupacao(aquario, ocupados)
                val status = Repositorio.statusOcupacao(percentual)
                LinhaInfo(
                    icone = Icons.Filled.WaterDrop,
                    rotulo = aquario.nome,
                    valor = "${Repositorio.volumeLitros(aquario)} L"
                )
                LinhaInfo(
                    icone = Icons.Filled.WaterDrop,
                    rotulo = "Tipo",
                    valor = aquario.tipo.rotulo
                )
                LinhaInfo(
                    icone = Icons.Filled.WaterDrop,
                    rotulo = "Ocupação",
                    valor = "$percentual% · ${status.rotulo}"
                )
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ChipRotulado(
                        texto = status.rotulo,
                        corFundo = corSuaveDoStatus(status),
                        corTexto = corDoStatus(status)
                    )
                    Spacer(Modifier.weight(1f))
                    Button(onClick = aoAbrirAquario, shape = RoundedCornerShape(12.dp)) {
                        Text("Ver aquário")
                    }
                }
            }
        }
    }
}
