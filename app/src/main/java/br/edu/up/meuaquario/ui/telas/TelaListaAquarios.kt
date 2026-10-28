package br.edu.up.meuaquario.ui.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.up.meuaquario.dados.Aquario
import br.edu.up.meuaquario.dados.Especie
import br.edu.up.meuaquario.dados.Repositorio
import br.edu.up.meuaquario.navegacao.Rotas
import br.edu.up.meuaquario.ui.componentes.CartaoAquario
import br.edu.up.meuaquario.ui.componentes.EstadoVazio
import br.edu.up.meuaquario.ui.componentes.lembrarGatilhoDeRecarga
import br.edu.up.meuaquario.ui.theme.AquaClaraSuave
import br.edu.up.meuaquario.ui.theme.AquaEscura
import br.edu.up.meuaquario.ui.theme.AquaPrimaria
import br.edu.up.meuaquario.ui.theme.AquaSecundaria
import br.edu.up.meuaquario.ui.theme.TextoSecundario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaListaAquarios(navController: NavHostController) {

    val gatilho = lembrarGatilhoDeRecarga()
    var aquarios by remember { mutableStateOf<List<Aquario>>(emptyList()) }
    var especies by remember { mutableStateOf<List<Especie>>(emptyList()) }

    fun recarregar() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val a = Repositorio.buscarAquarios()
                val e = Repositorio.buscarEspecies()
                withContext(Dispatchers.Main) {
                    aquarios = a
                    especies = e
                }
            } catch (ex: Exception) {
                android.util.Log.e("TelaListaAquarios", "Erro ao carregar", ex)
            }
        }
    }

    LaunchedEffect(gatilho) { recarregar() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Meus aquários") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AquaPrimaria,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Rotas.NOVO_AQUARIO) },
                containerColor = AquaSecundaria,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Novo aquário") }
            )
        }
    ) { espacamento ->

        if (aquarios.isEmpty()) {
            EstadoVazio(
                icone = Icons.Filled.WaterDrop,
                titulo = "Nenhum aquário cadastrado",
                mensagem = "Toque em “Novo aquário” e informe as medidas em centímetros: " +
                    "o app calcula os litros sozinho.",
                modifier = Modifier.padding(espacamento)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(espacamento),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 104.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { ResumoDaLista(aquarios, especies) }

                items(items = aquarios, key = { it.id }) { aquario ->
                    val especiesDoAquario = especies.filter { it.aquarioId == aquario.id }
                    CartaoAquario(
                        aquario = aquario,
                        especiesDoAquario = especiesDoAquario,
                        aoClicar = { navController.navigate(Rotas.detalheAquario(aquario.id)) },
                        aoRemover = {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    Repositorio.removerAquario(aquario.id)
                                    recarregar()
                                } catch (ex: Exception) {
                                    android.util.Log.e("TelaListaAquarios", "Erro ao remover", ex)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ResumoDaLista(aquarios: List<Aquario>, especies: List<Especie>) {
    val volume = Repositorio.volumeTotal(aquarios)
    val ocupados = Repositorio.litrosOcupados(especies)
    val percentual = if (volume > 0) (ocupados * 100) / volume else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AquaClaraSuave)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Waves, contentDescription = null, tint = AquaEscura)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "${aquarios.size} aquários · $volume L no total",
                    style = MaterialTheme.typography.titleMedium,
                    color = AquaEscura
                )
                Text(
                    text = "$ocupados L ocupados pelos peixes ($percentual%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
        }
    }
}
