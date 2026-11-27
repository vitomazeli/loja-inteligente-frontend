package br.com.lojainteligente.frontend.ui.screens.vendas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.model.VendaResponseApi
import br.com.lojainteligente.frontend.ui.components.KoraActionIcon
import br.com.lojainteligente.frontend.ui.components.KoraConfirmationDialog
import br.com.lojainteligente.frontend.ui.components.KoraDataCard
import br.com.lojainteligente.frontend.ui.components.KoraEmptyState
import br.com.lojainteligente.frontend.ui.components.KoraIconAction
import br.com.lojainteligente.frontend.ui.components.KoraLoadingState
import br.com.lojainteligente.frontend.ui.components.KoraSearchField
import br.com.lojainteligente.frontend.ui.components.KoraSectionHeader
import br.com.lojainteligente.frontend.ui.components.KoraStatusBadge
import br.com.lojainteligente.frontend.ui.components.KoraStatusTone
import br.com.lojainteligente.frontend.ui.components.koraFormatarMoeda
import br.com.lojainteligente.frontend.ui.components.koraNonTechnicalOr
import br.com.lojainteligente.frontend.viewmodel.VendaViewModel

@Composable
fun VendaLista(
    viewModel: VendaViewModel,
    atualizarVersao: Int = 0,
    aoEditar: (VendaResponseApi) -> Unit,
    aoNovaVenda: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()
    var busca by remember { mutableStateOf("") }
    var vendaParaExcluir by remember { mutableStateOf<VendaResponseApi?>(null) }
    val filtradas = estado.vendas.filter { venda ->
        val termo = busca.trim()
        termo.isBlank() || listOfNotNull(venda.id?.toString(), venda.clienteNome, venda.status, venda.observacao)
            .plus(venda.itens.mapNotNull { it.produtoNome })
            .any { it.contains(termo, ignoreCase = true) }
    }

    LaunchedEffect(viewModel, atualizarVersao) { viewModel.carregar() }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        KoraSectionHeader(
            title = "Histórico de vendas",
            count = estado.vendas.size,
            addLabel = "＋ Nova venda",
            onAdd = aoNovaVenda,
            onRefresh = viewModel::carregar,
            refreshing = estado.carregando
        )
        KoraSearchField(busca, { busca = it }, "Buscar por ID, cliente, status ou produto")

        if (estado.mensagem.isNotBlank()) {
            Text(
                estado.mensagem,
                color = if (estado.mensagem.contains("sucesso", true) || estado.mensagem.contains("exclu", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }

        when {
            estado.carregando && estado.vendas.isEmpty() -> KoraLoadingState("Carregando vendas...")
            filtradas.isEmpty() && estado.vendas.isEmpty() -> KoraEmptyState("Ainda não há vendas", "Registre uma venda para acompanhar a movimentação da loja.")
            filtradas.isEmpty() -> KoraEmptyState("Nenhuma venda encontrada", "Tente outro ID, cliente, status ou produto.")
            else -> LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtradas, key = { venda -> venda.id ?: "${venda.dataVenda}-${venda.clienteId}" }) { venda ->
                    KoraDataCard(contentPadding = 14.dp, contentSpacing = 8.dp) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Venda · ID ${venda.id ?: "—"}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                Text(venda.clienteNome.koraNonTechnicalOr("Cliente não informado"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Data: ${formatarDataVenda(venda.dataVenda.koraNonTechnicalOr(""))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                Text(koraFormatarMoeda(venda.total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                KoraStatusBadge(rotuloStatusVenda(venda.status), tomStatusVenda(venda.status))
                            }
                        }
                        if (!venda.observacao.isNullOrBlank() && !venda.observacao.trim().equals("string", ignoreCase = true)) {
                            Text("Observação: ${venda.observacao}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                        Text("Itens da venda · ${venda.itens.size}", style = MaterialTheme.typography.titleSmall)
                        venda.itens.forEach { item ->
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f), shape = RoundedCornerShape(14.dp)) {
                                Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(item.produtoNome.koraNonTechnicalOr("Produto não informado"), fontWeight = FontWeight.Medium)
                                        Text("Quantidade: ${item.quantidade} · Unitário: ${koraFormatarMoeda(item.precoUnitario)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(koraFormatarMoeda(item.subtotal), style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            KoraIconAction(KoraActionIcon.Edit, onClick = { aoEditar(venda) })
                            KoraIconAction(KoraActionIcon.Delete, onClick = { vendaParaExcluir = venda }, enabled = !estado.carregando)
                        }
                    }
                }
            }
        }
    }

    vendaParaExcluir?.let { venda ->
        KoraConfirmationDialog(
            title = "Excluir venda?",
            message = "A venda ID ${venda.id ?: "—"} será removida. Esta ação não pode ser desfeita.",
            onConfirm = {
                venda.id?.let(viewModel::excluir)
                vendaParaExcluir = null
            },
            onDismiss = { vendaParaExcluir = null }
        )
    }
}

private fun tomStatusVenda(status: String): KoraStatusTone = when (normalizarStatusVenda(status)) {
    "CONCLUIDA" -> KoraStatusTone.Success
    "PENDENTE" -> KoraStatusTone.Warning
    else -> KoraStatusTone.Neutral
}

private fun formatarDataVenda(data: String): String {
    if (data.isBlank()) return "Data não disponível"
    return try {
        val partes = data.substringBefore(".").split("T")
        if (partes.size != 2) data else {
            val dataPartes = partes[0].split("-")
            if (dataPartes.size != 3) data else "${dataPartes[2]}/${dataPartes[1]}/${dataPartes[0]} ${partes[1].take(5)}"
        }
    } catch (_: Exception) { data }
}
