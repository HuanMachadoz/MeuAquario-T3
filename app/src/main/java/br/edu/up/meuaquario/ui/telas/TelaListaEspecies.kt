package br.edu.up.meuaquario.ui.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.up.meuaquario.dados.Especie
import br.edu.up.meuaquario.dados.Repositorio
import br.edu.up.meuaquario.navegacao.Rotas
import br.edu.up.meuaquario.ui.componentes.CartaoEspecie
import br.edu.up.meuaquario.ui.componentes.EstadoVazio
import br.edu.up.meuaquario.ui.componentes.lembrarGatilhoDeRecarga
import br.edu.up.meuaquario.ui.theme.AquaPrimaria
import br.edu.up.meuaquario.ui.theme.AquaSecundaria
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaListaEspecies(navController: NavHostController) {

    val gatilho = lembrarGatilhoDeRecarga()
    var especies by remember { mutableStateOf<List<Especie>>(emptyList()) }
    val marcadas = remember { mutableStateListOf<Int>() }

    fun recarregar() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val e = Repositorio.buscarEspecies()
                withContext(Dispatchers.Main) { especies = e }
            } catch (ex: Exception) {
                android.util.Log.e("TelaListaEspecies", "Erro ao carregar", ex)
            }
        }
    }

    LaunchedEffect(gatilho) { recarregar() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Espécies") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AquaPrimaria,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Rotas.novaEspecie()) },
                containerColor = AquaSecundaria,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nova espécie") }
            )
        }
    ) { espacamento ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
        ) {
            if (marcadas.isNotEmpty()) {
                BarraDeMarcadas(
                    quantidade = marcadas.size,
                    aoCancelar = { marcadas.clear() },
                    aoRemover = {
                        val ids = marcadas.toSet()
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                Repositorio.removerEspecies(ids)
                                withContext(Dispatchers.Main) {
                                    marcadas.clear()
                                    recarregar()
                                }
                            } catch (ex: Exception) {
                                android.util.Log.e("TelaListaEspecies", "Erro ao remover em lote", ex)
                            }
                        }
                    }
                )
            }

            if (especies.isEmpty()) {
                EstadoVazio(
                    icone = Icons.Filled.SetMeal,
                    titulo = "Nenhuma espécie cadastrada",
                    mensagem = "Toque em “Nova espécie” para cadastrar o primeiro peixe " +
                        "e escolher em qual aquário ele vive."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 104.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = especies, key = { it.id }) { especie ->
                        CartaoEspecie(
                            especie = especie,
                            marcada = marcadas.contains(especie.id),
                            aoClicar = { navController.navigate(Rotas.detalheEspecie(especie.id)) },
                            aoMarcar = {
                                if (marcadas.contains(especie.id)) {
                                    marcadas.remove(especie.id)
                                } else {
                                    marcadas.add(especie.id)
                                }
                            },
                            aoRemover = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        Repositorio.removerEspecie(especie.id)
                                        withContext(Dispatchers.Main) {
                                            marcadas.remove(especie.id)
                                            recarregar()
                                        }
                                    } catch (ex: Exception) {
                                        android.util.Log.e("TelaListaEspecies", "Erro ao remover", ex)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BarraDeMarcadas(
    quantidade: Int,
    aoCancelar: () -> Unit,
    aoRemover: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AquaPrimaria)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$quantidade marcada(s)",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = aoCancelar) {
                Text("Cancelar", color = Color.White)
            }
            TextButton(onClick = aoRemover) {
                Text("Remover", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
