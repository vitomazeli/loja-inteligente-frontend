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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.model.VendaResponseApi
import br.com.lojainteligente.frontend.ui.components.KoraEmptyState
import br.com.lojainteligente.frontend.ui.components.KoraSectionHeader
import br.com.lojainteligente.frontend.viewmodel.VendaViewModel

@Composable
fun VendaLista(
    viewModel: VendaViewModel,
    atualizarVersao: Int = 0,
    aoEditar: (VendaResponseApi) -> Unit,
    aoNovaVenda: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()
    LaunchedEffect(viewModel, atualizarVersao) { viewModel.carregar() }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        KoraSectionHeader(
            title = "Vendas recentes",
            count = estado.vendas.size,
            addLabel = "＋ Nova venda",
            onAdd = aoNovaVenda,
            onRefresh = { viewModel.carregar() },
            refreshing = estado.carregando
        )
        if (estado.carregando && estado.vendas.isEmpty()) CircularProgressIndicator()
        if (estado.mensagem.isNotBlank()) {
            Text(estado.mensagem, color = if (estado.mensagem.contains("exclu", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        }
        if (!estado.carregando && estado.vendas.isEmpty() && estado.mensagem.isBlank()) {
            KoraEmptyState("Ainda não há vendas", "Registre uma venda para acompanhar a movimentação da loja.")
        }
        if (estado.vendas.isNotEmpty()) {
            LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(estado.vendas, key = { venda -> venda.id ?: "venda-${estado.vendas.indexOf(venda)}" }) { venda ->
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Venda · ID ${venda.id ?: "—"}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                    Text(venda.clienteNome ?: "Cliente não informado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = androidx.compose.ui.Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(formatarMoeda(venda.total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(venda.status, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text("Data: ${formatarDataVenda(venda.dataVenda ?: "")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (!venda.observacao.isNullOrBlank() && !venda.observacao.trim().equals("string", ignoreCase = true)) {
                                Text(venda.observacao, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                            Text("Itens da venda · ${venda.itens.size}", style = MaterialTheme.typography.titleSmall)
                            venda.itens.forEach { item ->
                                Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f), shape = RoundedCornerShape(14.dp)) {
                                    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                            Text(item.produtoNome ?: "Produto não informado", fontWeight = FontWeight.Medium)
                                            Text("Quantidade: ${item.quantidade} · Unitário: ${formatarMoeda(item.precoUnitario)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(formatarMoeda(item.subtotal), style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { aoEditar(venda) }) { Text("✎  Editar") }
                                TextButton(enabled = !estado.carregando, onClick = { venda.id?.let(viewModel::excluir) }) { Text("Excluir") }
                            }
                        }
                    }
                }
            }
        }
    }
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

private fun formatarMoeda(valor: Double): String {
    val centavos = kotlin.math.round(valor * 100).toLong()
    val inteiro = centavos / 100
    val decimal = kotlin.math.abs(centavos % 100)
    return "R$ $inteiro,${decimal.toString().padStart(2, '0')}"
}
