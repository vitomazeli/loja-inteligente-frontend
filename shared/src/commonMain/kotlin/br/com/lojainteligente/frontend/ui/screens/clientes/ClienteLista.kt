package br.com.lojainteligente.frontend.ui.screens.clientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.model.ClienteResponseApi
import br.com.lojainteligente.frontend.ui.components.KoraActionIcon
import br.com.lojainteligente.frontend.ui.components.KoraConfirmationDialog
import br.com.lojainteligente.frontend.ui.components.KoraDataCard
import br.com.lojainteligente.frontend.ui.components.KoraEmptyState
import br.com.lojainteligente.frontend.ui.components.KoraIconAction
import br.com.lojainteligente.frontend.ui.components.KoraInitialsAvatar
import br.com.lojainteligente.frontend.ui.components.KoraLoadingState
import br.com.lojainteligente.frontend.ui.components.KoraSearchField
import br.com.lojainteligente.frontend.ui.components.KoraSectionHeader
import br.com.lojainteligente.frontend.ui.components.KoraStatusBadge
import br.com.lojainteligente.frontend.ui.components.koraNonTechnicalOr
import br.com.lojainteligente.frontend.viewmodel.ClienteViewModel

@Composable
fun ClienteLista(
    viewModel: ClienteViewModel,
    atualizarVersao: Int = 0,
    aoEditar: (ClienteResponseApi) -> Unit,
    aoNovoCliente: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()
    var busca by remember { mutableStateOf("") }
    var clienteParaExcluir by remember { mutableStateOf<ClienteResponseApi?>(null) }
    val filtrados = estado.clientes.filter { cliente ->
        val termo = busca.trim()
        termo.isBlank() || listOfNotNull(cliente.nome, cliente.email, cliente.telefone, cliente.id.toString()).any {
            it.contains(termo, ignoreCase = true)
        }
    }

    LaunchedEffect(viewModel, atualizarVersao) { viewModel.carregar() }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        KoraSectionHeader(
            title = "Clientes cadastrados",
            count = estado.clientes.size,
            addLabel = "＋ Novo cliente",
            onAdd = aoNovoCliente,
            onRefresh = viewModel::carregar,
            refreshing = estado.carregando
        )
        KoraSearchField(busca, { busca = it }, "Buscar por nome, e-mail ou telefone")

        if (estado.mensagem.isNotBlank()) {
            Text(
                estado.mensagem,
                color = if (estado.mensagem.contains("sucesso", true) || estado.mensagem.contains("exclu", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }

        when {
            estado.carregando && estado.clientes.isEmpty() -> KoraLoadingState("Carregando clientes...")
            filtrados.isEmpty() && estado.clientes.isEmpty() -> KoraEmptyState("Ainda não há clientes", "Cadastre clientes para agilizar o registro das vendas.")
            filtrados.isEmpty() -> KoraEmptyState("Nenhum cliente encontrado", "Tente outro nome, e-mail ou telefone.")
            else -> LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtrados, key = { it.id }) { cliente ->
                    KoraDataCard(contentPadding = 14.dp, contentSpacing = 7.dp) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            KoraInitialsAvatar(cliente.nome.koraNonTechnicalOr("Cliente"))
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(cliente.nome.koraNonTechnicalOr("Cliente sem nome"), style = MaterialTheme.typography.titleMedium)
                                Text("ID: ${cliente.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            KoraStatusBadge(cliente.ativo)
                        }
                        Text("E-mail: ${cliente.email.koraNonTechnicalOr("Não informado")}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Telefone: ${cliente.telefone.koraNonTechnicalOr("Não informado")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            KoraIconAction(KoraActionIcon.Edit, onClick = { aoEditar(cliente) })
                            KoraIconAction(KoraActionIcon.Delete, onClick = { clienteParaExcluir = cliente }, enabled = !estado.carregando)
                        }
                    }
                }
            }
        }
    }

    clienteParaExcluir?.let { cliente ->
        KoraConfirmationDialog(
            title = "Excluir cliente?",
            message = "O cadastro de “${cliente.nome}” será removido. Esta ação não pode ser desfeita.",
            onConfirm = {
                viewModel.excluir(cliente.id)
                clienteParaExcluir = null
            },
            onDismiss = { clienteParaExcluir = null }
        )
    }
}
