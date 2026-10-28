package br.edu.up.meuaquario.ui.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.edu.up.meuaquario.dados.Aquario
import br.edu.up.meuaquario.dados.Especie
import br.edu.up.meuaquario.dados.Repositorio
import br.edu.up.meuaquario.dados.StatusOcupacao
import br.edu.up.meuaquario.dados.TipoAquario
import br.edu.up.meuaquario.ui.theme.AmareloAtencao
import br.edu.up.meuaquario.ui.theme.AmareloSuave
import br.edu.up.meuaquario.ui.theme.AquaClaraSuave
import br.edu.up.meuaquario.ui.theme.AquaEscura
import br.edu.up.meuaquario.ui.theme.AquaPrimaria
import br.edu.up.meuaquario.ui.theme.AquaSecundaria
import br.edu.up.meuaquario.ui.theme.AquaTerciaria
import br.edu.up.meuaquario.ui.theme.Contorno
import br.edu.up.meuaquario.ui.theme.SuperficieVariante
import br.edu.up.meuaquario.ui.theme.TextoPrincipal
import br.edu.up.meuaquario.ui.theme.TextoSecundario
import br.edu.up.meuaquario.ui.theme.VermelhoAlerta
import br.edu.up.meuaquario.ui.theme.VermelhoSuave
import br.edu.up.meuaquario.ui.theme.VerdeOk
import br.edu.up.meuaquario.ui.theme.VerdeSuave

fun corDoStatus(status: StatusOcupacao): Color = when (status) {
    StatusOcupacao.LIVRE -> VerdeOk
    StatusOcupacao.ADEQUADO -> AquaSecundaria
    StatusOcupacao.ATENCAO -> AmareloAtencao
    StatusOcupacao.SUPERLOTADO -> VermelhoAlerta
}

fun corSuaveDoStatus(status: StatusOcupacao): Color = when (status) {
    StatusOcupacao.LIVRE -> VerdeSuave
    StatusOcupacao.ADEQUADO -> AquaClaraSuave
    StatusOcupacao.ATENCAO -> AmareloSuave
    StatusOcupacao.SUPERLOTADO -> VermelhoSuave
}

fun corDoTipo(tipo: TipoAquario): Color = when (tipo) {
    TipoAquario.DOCE -> AquaSecundaria
    TipoAquario.PLANTADO -> VerdeOk
    TipoAquario.MARINHO -> AquaTerciaria
}

@Composable
fun ChipRotulado(
    texto: String,
    corFundo: Color,
    corTexto: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(corFundo)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = texto,
            color = corTexto,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun CabecalhoSecao(titulo: String, modifier: Modifier = Modifier) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleMedium,
        color = TextoPrincipal,
        modifier = modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun CartaoEstatistica(
    icone: ImageVector,
    valor: String,
    rotulo: String,
    cor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(cor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icone,
                    contentDescription = null,
                    tint = cor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipal
            )
            Text(
                text = rotulo,
                style = MaterialTheme.typography.labelSmall,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BarraOcupacao(
    fracao: Float,
    cor: Color,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = { fracao },
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(50)),
        color = cor,
        trackColor = cor.copy(alpha = 0.18f)
    )
}

@Composable
fun LinhaInfo(
    icone: ImageVector,
    rotulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = AquaPrimaria,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = rotulo,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextoPrincipal
        )
    }
}

@Composable
fun EstadoVazio(
    icone: ImageVector,
    titulo: String,
    mensagem: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = Contorno,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            color = TextoPrincipal,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = mensagem,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CartaoAquario(
    aquario: Aquario,
    especiesDoAquario: List<Especie>,
    aoClicar: () -> Unit,
    aoRemover: () -> Unit,
    modifier: Modifier = Modifier
) {
    val volume = Repositorio.volumeLitros(aquario)
    val ocupados = Repositorio.litrosOcupados(especiesDoAquario)
    val totalPeixes = especiesDoAquario.sumOf { it.quantidade }
    val percentual = Repositorio.percentualOcupacao(aquario, ocupados)
    val status = Repositorio.statusOcupacao(percentual)
    val corTipo = corDoTipo(aquario.tipo)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = aoClicar),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(corTipo.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = corTipo
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = aquario.nome,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoPrincipal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${aquario.comprimentoCm} × ${aquario.larguraCm} × " +
                            "${aquario.alturaCm} cm  ·  $volume L",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                }
                IconButton(onClick = aoRemover) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Remover ${aquario.nome}",
                        tint = VermelhoAlerta
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                ChipRotulado(aquario.tipo.rotulo, corTipo.copy(alpha = 0.13f), corTipo)
                Spacer(Modifier.width(6.dp))
                ChipRotulado("$totalPeixes peixes", SuperficieVariante, TextoSecundario)
                Spacer(Modifier.width(6.dp))
                ChipRotulado("${especiesDoAquario.size} espécies", SuperficieVariante, TextoSecundario)
            }

            Spacer(Modifier.height(12.dp))

            BarraOcupacao(Repositorio.fracaoOcupacao(percentual), corDoStatus(status))

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$ocupados L ocupados de $volume L",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoSecundario,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$percentual% · ${status.rotulo}",
                    style = MaterialTheme.typography.labelSmall,
                    color = corDoStatus(status),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartaoEspecie(
    especie: Especie,
    marcada: Boolean,
    aoClicar: () -> Unit,
    aoMarcar: () -> Unit,
    aoRemover: () -> Unit,
    modifier: Modifier = Modifier
) {
    val litros = Repositorio.litrosNecessarios(especie)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = aoClicar, onLongClick = aoMarcar),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (marcada) AquaClaraSuave else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (marcada) 0.dp else 2.dp),
        border = if (marcada) BorderStroke(2.dp, AquaPrimaria) else null
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AquaClaraSuave),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SetMeal,
                    contentDescription = null,
                    tint = AquaPrimaria
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = especie.nome,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = especie.nomeCientifico,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ChipRotulado("×${especie.quantidade}", AquaClaraSuave, AquaEscura)
                    Spacer(Modifier.width(6.dp))
                    ChipRotulado("${especie.tamanhoAdultoCm} cm", SuperficieVariante, TextoSecundario)
                    Spacer(Modifier.width(6.dp))
                    ChipRotulado("$litros L", SuperficieVariante, TextoSecundario)
                }
            }
            IconButton(onClick = aoRemover) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remover ${especie.nome}",
                    tint = VermelhoAlerta
                )
            }
        }
    }
}

@Composable
fun TelaItemRemovido(aoVoltar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = AmareloAtencao,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Item não encontrado",
            style = MaterialTheme.typography.titleMedium,
            color = TextoPrincipal
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Ele pode ter sido removido da lista.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        Button(onClick = aoVoltar) {
            Text("Voltar")
        }
    }
}
