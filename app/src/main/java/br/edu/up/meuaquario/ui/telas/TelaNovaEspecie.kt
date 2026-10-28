package br.edu.up.meuaquario.ui.telas

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.up.meuaquario.dados.Aquario
import br.edu.up.meuaquario.dados.Repositorio
import br.edu.up.meuaquario.navegacao.Rotas
import br.edu.up.meuaquario.ui.componentes.BarraOcupacao
import br.edu.up.meuaquario.ui.componentes.CabecalhoSecao
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNovaEspecie(navController: NavHostController, aquarioPreSelecionadoId: Int) {

    var aquarios by remember { mutableStateOf<List<Aquario>>(emptyList()) }
    var especies by remember { mutableStateOf<List<br.edu.up.meuaquario.dados.Especie>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val a = Repositorio.buscarAquarios()
            val e = Repositorio.buscarEspecies()
            withContext(Dispatchers.Main) {
                aquarios = a
                especies = e
            }
        } catch (ex: Exception) {
            android.util.Log.e("TelaNovaEspecie", "Erro ao carregar aquarios", ex)
        }
    }

    var nome by rememberSaveable { mutableStateOf("") }
    var nomeCientifico by rememberSaveable { mutableStateOf("") }
    var quantidade by rememberSaveable { mutableStateOf("1") }
    var tamanho by rememberSaveable { mutableStateOf("") }
    var ph by rememberSaveable { mutableStateOf("7.0") }
    var aquarioEscolhidoId by rememberSaveable { mutableStateOf(aquarioPreSelecionadoId) }
    var tentouSalvar by remember { mutableStateOf(false) }

    val qtd = quantidade.toIntOrNull() ?: 0
    val tam = tamanho.toIntOrNull() ?: 0
    val phValor = ph.replace(",", ".").toDoubleOrNull() ?: 7.0

    val nomeValido = nome.trim().length >= 2
    val quantidadeValida = qtd in 1..500
    val tamanhoValido = tam in 1..200
    val aquarioValido = aquarios.any { it.id == aquarioEscolhidoId }
    val formularioValido = nomeValido && quantidadeValida && tamanhoValido && aquarioValido

    val litrosPrevistos = tam * qtd

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Nova espécie") },
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

            if (aquarios.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SuperficieVariante)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = VermelhoAlerta
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Cadastre um aquário primeiro",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipal
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Toda espécie precisa de um aquário para viver. " +
                                "Crie um aquário e volte aqui.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { navController.navigate(Rotas.NOVO_AQUARIO) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Criar aquário")
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome popular") },
                placeholder = { Text("Ex.: Neon Cardinal") },
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

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = nomeCientifico,
                onValueChange = { nomeCientifico = it },
                label = { Text("Nome científico (opcional)") },
                placeholder = { Text("Ex.: Paracheirodon innesi") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            CabecalhoSecao("Quantos e de que tamanho")

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = quantidade,
                    onValueChange = { bruto -> quantidade = bruto.filter { it.isDigit() }.take(3) },
                    label = { Text("Quantidade") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = tentouSalvar && !quantidadeValida,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = tamanho,
                    onValueChange = { bruto -> tamanho = bruto.filter { it.isDigit() }.take(3) },
                    label = { Text("Tamanho adulto") },
                    suffix = { Text("cm") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = tentouSalvar && !tamanhoValido,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = ph,
                onValueChange = { bruto -> ph = bruto.take(4) },
                label = { Text("pH ideal da água") },
                placeholder = { Text("Ex.: 6.8") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp)
            )

            CabecalhoSecao("Em qual aquário esta espécie vai viver")

            if (aquarios.isEmpty()) {
                Text(
                    text = "Nenhum aquário cadastrado ainda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            } else {
                aquarios.forEach { aquario ->
                    LinhaEscolhaDeAquario(
                        aquario = aquario,
                        especiesDoAquario = especies.filter { it.aquarioId == aquario.id },
                        selecionado = aquario.id == aquarioEscolhidoId,
                        aoEscolher = { aquarioEscolhidoId = aquario.id }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            if (tentouSalvar && !aquarioValido && aquarios.isNotEmpty()) {
                Text(
                    text = "Escolha um aquário para a espécie.",
                    style = MaterialTheme.typography.labelSmall,
                    color = VermelhoAlerta
                )
            }

            Spacer(Modifier.height(18.dp))

            PreviaDaEspecie(
                litrosPrevistos = litrosPrevistos,
                aquario = aquarios.firstOrNull { it.id == aquarioEscolhidoId },
                especiesDoAquario = especies.filter { it.aquarioId == aquarioEscolhidoId },
                dadosOk = quantidadeValida && tamanhoValido
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (formularioValido) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                Repositorio.inserirEspecie(
                                    nome = nome,
                                    nomeCientifico = nomeCientifico.ifBlank { "—" },
                                    aquarioId = aquarioEscolhidoId,
                                    quantidade = qtd,
                                    tamanhoAdultoCm = tam,
                                    phIdeal = phValor
                                )
                                withContext(Dispatchers.Main) { navController.popBackStack() }
                            } catch (ex: Exception) {
                                android.util.Log.e("TelaNovaEspecie", "Erro ao salvar", ex)
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
                Text("Salvar espécie")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LinhaEscolhaDeAquario(
    aquario: Aquario,
    especiesDoAquario: List<br.edu.up.meuaquario.dados.Especie>,
    selecionado: Boolean,
    aoEscolher: () -> Unit
) {
    val ocupados = Repositorio.litrosOcupados(especiesDoAquario)
    val percentual = Repositorio.percentualOcupacao(aquario, ocupados)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { aoEscolher() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selecionado) AquaClaraSuave else MaterialTheme.colorScheme.surface
        ),
        border = if (selecionado) BorderStroke(2.dp, AquaPrimaria) else null
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.WaterDrop,
                contentDescription = null,
                tint = if (selecionado) AquaPrimaria else TextoSecundario
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = aquario.nome,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoPrincipal
                )
                Text(
                    text = "${aquario.tipo.rotulo} · ${Repositorio.volumeLitros(aquario)} L · " +
                        "$percentual% ocupado",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoSecundario
                )
            }
            if (selecionado) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Aquário escolhido",
                    tint = AquaPrimaria
                )
            }
        }
    }
}

@Composable
private fun PreviaDaEspecie(
    litrosPrevistos: Int,
    aquario: Aquario?,
    especiesDoAquario: List<br.edu.up.meuaquario.dados.Especie>,
    dadosOk: Boolean
) {
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

            if (!dadosOk) {
                Text(
                    text = "Informe a quantidade e o tamanho adulto para ver a prévia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            } else {

                Text(
                    text = "Esta espécie vai ocupar $litrosPrevistos litros",
                    style = MaterialTheme.typography.titleLarge,
                    color = AquaPrimaria,
                    fontWeight = FontWeight.Bold
                )

                if (aquario != null) {
                    val volume = Repositorio.volumeLitros(aquario)
                    val ocupadosHoje = Repositorio.litrosOcupados(especiesDoAquario)
                    val depois = ocupadosHoje + litrosPrevistos
                    val percentualDepois = if (volume > 0) (depois * 100) / volume else 0
                    val estoura = volume > 0 && depois > volume

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "“${aquario.nome}” tem $volume L e está com $ocupadosHoje L ocupados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                    Spacer(Modifier.height(8.dp))
                    BarraOcupacao(
                        fracao = (percentualDepois / 100f).coerceIn(0f, 1f),
                        cor = if (estoura) VermelhoAlerta else AquaSecundaria
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Depois de salvar: $depois L de $volume L · $percentualDepois%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (estoura) VermelhoAlerta else TextoSecundario
                    )
                    if (estoura) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Atenção: com esta quantidade o aquário fica superlotado.",
                            style = MaterialTheme.typography.labelSmall,
                            color = VermelhoAlerta,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
