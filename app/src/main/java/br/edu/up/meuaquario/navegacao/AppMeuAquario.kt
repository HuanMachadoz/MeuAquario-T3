package br.edu.up.meuaquario.navegacao

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.up.meuaquario.dados.Repositorio
import br.edu.up.meuaquario.ui.telas.TelaDetalheAquario
import br.edu.up.meuaquario.ui.telas.TelaDetalheEspecie
import br.edu.up.meuaquario.ui.telas.TelaInicio
import br.edu.up.meuaquario.ui.telas.TelaListaAquarios
import br.edu.up.meuaquario.ui.telas.TelaListaEspecies
import br.edu.up.meuaquario.ui.telas.TelaNovaEspecie
import br.edu.up.meuaquario.ui.telas.TelaNovoAquario

@Composable
fun AppMeuAquario() {

    val navController = rememberNavController()
    val context = LocalContext.current
    var pronto by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Repositorio.iniciar(context)
        pronto = true
    }

    if (!pronto) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Carregando...", color = MaterialTheme.colorScheme.onBackground)
        }
        return
    }

    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route
    val mostrarBarraInferior = rotaAtual != null && rotaAtual in Rotas.ITENS_DA_BARRA

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (mostrarBarraInferior) {
                BarraInferior(navController = navController, rotaAtual = rotaAtual)
            }
        }
    ) { espacamento ->

        NavHost(
            navController = navController,
            startDestination = Rotas.INICIO,
            modifier = Modifier.padding(espacamento)
        ) {

            composable(Rotas.INICIO) { TelaInicio(navController) }
            composable(Rotas.AQUARIOS) { TelaListaAquarios(navController) }
            composable(Rotas.ESPECIES) { TelaListaEspecies(navController) }

            composable(Rotas.NOVO_AQUARIO) { TelaNovoAquario(navController) }

            composable(
                route = Rotas.NOVA_ESPECIE,
                arguments = listOf(
                    navArgument(Rotas.ARG_AQUARIO_ID) {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { entrada ->
                TelaNovaEspecie(
                    navController = navController,
                    aquarioPreSelecionadoId = entrada.arguments?.getInt(Rotas.ARG_AQUARIO_ID) ?: -1
                )
            }

            composable(
                route = Rotas.DETALHE_AQUARIO,
                arguments = listOf(navArgument(Rotas.ARG_ID) { type = NavType.IntType })
            ) { entrada ->
                TelaDetalheAquario(
                    navController = navController,
                    aquarioId = entrada.arguments?.getInt(Rotas.ARG_ID) ?: -1
                )
            }

            composable(
                route = Rotas.DETALHE_ESPECIE,
                arguments = listOf(navArgument(Rotas.ARG_ID) { type = NavType.IntType })
            ) { entrada ->
                TelaDetalheEspecie(
                    navController = navController,
                    especieId = entrada.arguments?.getInt(Rotas.ARG_ID) ?: -1
                )
            }
        }
    }
}

private data class ItemBarra(
    val rota: String,
    val rotulo: String,
    val icone: ImageVector
)

@Composable
private fun BarraInferior(navController: NavHostController, rotaAtual: String?) {

    val itens = listOf(
        ItemBarra(Rotas.INICIO, "Início", Icons.Filled.Home),
        ItemBarra(Rotas.AQUARIOS, "Aquários", Icons.Filled.WaterDrop),
        ItemBarra(Rotas.ESPECIES, "Espécies", Icons.Filled.SetMeal)
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        itens.forEach { item ->
            NavigationBarItem(
                selected = rotaAtual == item.rota,
                onClick = {
                    if (rotaAtual != item.rota) {
                        navController.navigate(item.rota) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icone, contentDescription = item.rotulo) },
                label = { Text(item.rotulo) }
            )
        }
    }
}
