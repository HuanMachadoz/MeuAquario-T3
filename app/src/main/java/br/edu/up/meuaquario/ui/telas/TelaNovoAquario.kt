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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.up.meuaquario.dados.Repositorio
import br.edu.up.meuaquario.dados.TipoAquario
import br.edu.up.meuaquario.ui.componentes.CabecalhoSecao
import br.edu.up.meuaquario.ui.theme.AquaClaraSuave
import br.edu.up.meuaquario.ui.theme.AquaEscura
import br.edu.up.meuaquario.ui.theme.AquaPrimaria
import br.edu.up.meuaquario.ui.theme.TextoSecundario
import br.edu.up.meuaquario.ui.theme.VermelhoAlerta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNovoAquario(navController: NavHostController) {

    var nome by rememberSaveable { mutableStateOf("") }
    var comprimento by rememberSaveable { mutableStateOf("") }
    var largura by rememberSaveable { mutableStateOf("") }
    var altura by rememberSaveable { mutableStateOf("") }
    var tipo by rememberSaveable { mutableStateOf(TipoAquario.DOCE) }
    var tentouSalvar by remember { mutableStateOf(false) }

    val c = comprimento.toIntOrNull() ?: 0
    val l = largura.toIntOrNull() ?: 0
    val a = altura.toIntOrNull() ?: 0
    val volumePrevisto = (c * l * a) / 1000

    val nomeValido = nome.trim().length >= 2
    val medidasValidas = c in 10..500 && l in 10..500 && a in 10..500
    val formularioValido = nomeValido && medidasValidas

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Novo aquário") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AquaPrimaria,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White
                )
            )
        }
    ) { espacamento ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do aquário") },
                placeholder = { Text("Ex.: Comunitário da sala") },
                leadingIcon = { Icon(Icons.Filled.WaterDrop, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = tentouSalvar && !nomeValido,
                supportingText = {
                    if (tentouSalvar && !nomeValido) {
                        Text("Escreva pelo menos 2 letras.", color = VermelhoAlerta)
                    }
                },
                shape = RoundedCornerShape(14.dp)
            )

            CabecalhoSecao("Medidas em centímetros")

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoNumerico(
                    valor = comprimento,
                    aoMudar = { comprimento = it },
                    rotulo = "Comprimento",
                    modifier = Modifier.weight(1f),
                    comErro = tentouSalvar && c !in 10..500
                )
                CampoNumerico(
                    valor = largura,
                    aoMudar = { largura = it },
                    rotulo = "Largura",
                    modifier = Modifier.weight(1f),
                    comErro = tentouSalvar && l !in 10..500
                )
                CampoNumerico(
                    valor = altura,
                    aoMudar = { altura = it },
                    rotulo = "Altura",
                    modifier = Modifier.weight(1f),
                    comErro = tentouSalvar && a !in 10..500
                )
            }

            if (tentouSalvar && !medidasValidas) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Cada medida precisa estar entre 10 e 500 cm.",
                    style = MaterialTheme.typography.labelSmall,
                    color = VermelhoAlerta
                )
            }

            CabecalhoSecao("Tipo de água")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoAquario.values().forEach { opcao ->
                    FilterChip(
                        selected = tipo == opcao,
                        onClick = { tipo = opcao },
                        label = { Text(opcao.rotulo) },
                        leadingIcon = {
                            if (tipo == opcao) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AquaClaraSuave,
                            selectedLabelColor = AquaEscura
                        )
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            PreviaDoVolume(volumePrevisto = volumePrevisto, medidasOk = medidasValidas)

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (formularioValido) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                Repositorio.inserirAquario(
                                    nome = nome,
                                    comprimentoCm = c,
                                    larguraCm = l,
                                    alturaCm = a,
                                    tipo = tipo
                                )
                                withContext(Dispatchers.Main) { navController.popBackStack() }
                            } catch (ex: Exception) {
                                android.util.Log.e("TelaNovoAquario", "Erro ao salvar", ex)
                            }
                        }
                    } else {
                        tentouSalvar = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Salvar aquário")
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Os litros são calculados automaticamente a partir das medidas.",
                style = MaterialTheme.typography.labelSmall,
                color = TextoSecundario
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CampoNumerico(
    valor: String,
    aoMudar: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    comErro: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = { bruto -> aoMudar(bruto.filter { it.isDigit() }.take(3)) },
        label = { Text(rotulo) },
        suffix = { Text("cm") },
        modifier = modifier,
        singleLine = true,
        isError = comErro,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
private fun PreviaDoVolume(volumePrevisto: Int, medidasOk: Boolean) {
    val peixesDeQuatroCm = volumePrevisto / 4

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AquaClaraSuave)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Prévia",
                style = MaterialTheme.typography.titleMedium,
                color = AquaEscura
            )
            Spacer(Modifier.height(6.dp))

            if (!medidasOk) {
                Text(
                    text = "Informe as três medidas para ver o volume calculado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            } else {
                Text(
                    text = "Volume: $volumePrevisto litros",
                    style = MaterialTheme.typography.titleLarge,
                    color = AquaPrimaria,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Dá para manter cerca de $peixesDeQuatroCm peixes de 4 cm " +
                        "(1 litro por centímetro de peixe).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
        }
    }
}
