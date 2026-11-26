package br.com.lojainteligente.frontend.ui.screens.clientes

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.model.ClienteResponseApi
import br.com.lojainteligente.frontend.ui.components.KoraEmptyState
import br.com.lojainteligente.frontend.ui.components.KoraSectionHeader
import br.com.lojainteligente.frontend.ui.components.KoraStatusBadge
import br.com.lojainteligente.frontend.viewmodel.ClienteViewModel

@Composable
fun ClienteLista(
    viewModel: ClienteViewModel,
    atualizarVersao: Int = 0,
    aoEditar: (ClienteResponseApi) -> Unit,
    aoNovoCliente: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()
    LaunchedEffect(viewModel, atualizarVersao) { viewModel.carregar() }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        KoraSectionHeader(
            title = "Clientes cadastrados",
            count = estado.clientes.size,
            addLabel = "＋ Novo cliente",
            onAdd = aoNovoCliente,
            onRefresh = { viewModel.carregar() },
            refreshing = estado.carregando
        )
        if (estado.carregando && estado.clientes.isEmpty()) CircularProgressIndicator()
        if (estado.mensagem.isNotBlank()) {
            Text(estado.mensagem, color = if (estado.mensagem.contains("sucesso", true) || estado.mensagem.contains("exclu", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        }
        if (!estado.carregando && estado.clientes.isEmpty() && estado.mensagem.isBlank()) {
            KoraEmptyState("Ainda não há clientes", "Cadastre clientes para agilizar o registro das vendas.")
        }
        if (estado.clientes.isNotEmpty()) {
            LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(estado.clientes, key = { it.id }) { cliente ->
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(cliente.nome, style = MaterialTheme.typography.titleMedium)
                                    Text("ID: ${cliente.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                KoraStatusBadge(cliente.ativo)
                            }
                            Text(cliente.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Telefone: ${cliente.telefone?.takeIf(String::isNotBlank) ?: "Não informado"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { aoEditar(cliente) }) { Text("✎  Editar") }
                                TextButton(enabled = !estado.carregando, onClick = { viewModel.excluir(cliente.id) }) { Text("Excluir") }
                            }
                        }
                    }
                }
            }
        }
    }
}
