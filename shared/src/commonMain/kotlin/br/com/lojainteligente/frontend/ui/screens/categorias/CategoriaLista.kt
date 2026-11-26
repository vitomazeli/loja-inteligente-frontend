package br.com.lojainteligente.frontend.ui.screens.categorias

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
import br.com.lojainteligente.frontend.data.repository.CategoriaResponseApi
import br.com.lojainteligente.frontend.ui.components.KoraEmptyState
import br.com.lojainteligente.frontend.ui.components.KoraSectionHeader
import br.com.lojainteligente.frontend.ui.components.KoraStatusBadge
import br.com.lojainteligente.frontend.viewmodel.CategoriaViewModel

@Composable
fun CategoriaLista(
    viewModel: CategoriaViewModel,
    modifier: Modifier = Modifier,
    atualizarVersao: Int = 0,
    aoEditar: (CategoriaResponseApi) -> Unit,
    aoNovaCategoria: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel, atualizarVersao) { viewModel.carregarCategorias() }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        KoraSectionHeader(
            title = "Categorias cadastradas",
            count = estado.categorias.size,
            addLabel = "＋ Nova categoria",
            onAdd = aoNovaCategoria,
            onRefresh = viewModel::carregarCategorias,
            refreshing = estado.carregando
        )

        if (estado.carregando && estado.categorias.isEmpty()) {
            CircularProgressIndicator()
        }
        if (estado.mensagem.isNotBlank()) {
            Text(
                estado.mensagem,
                color = if (estado.mensagem.contains("sucesso", true) || estado.mensagem.contains("exclu", true)) {
                    MaterialTheme.colorScheme.primary
                } else MaterialTheme.colorScheme.error
            )
        }

        if (estado.categorias.isEmpty() && !estado.carregando && estado.mensagem.isBlank()) {
            KoraEmptyState("Ainda não há categorias", "Crie uma categoria para organizar os produtos da loja.")
        } else if (estado.categorias.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.categorias, key = { it.id ?: "${it.nome}-${it.ordemExibicao}" }) { categoria ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(categoria.nome, style = MaterialTheme.typography.titleMedium)
                                    Text(categoria.descricao?.takeIf(String::isNotBlank) ?: "Sem descrição", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                KoraStatusBadge(categoria.ativo, "Ativa", "Inativa")
                            }
                            Text(
                                "ID: ${categoria.id ?: "—"}    ·    Ordem de exibição: ${categoria.ordemExibicao}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Cadastrada em ${formatarDataCadastro(categoria.dataCadastro)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { viewModel.selecionarCategoria(categoria); aoEditar(categoria) }) {
                                    Text("✎  Editar")
                                }
                                TextButton(
                                    onClick = { categoria.id?.let(viewModel::excluirCategoria) },
                                    enabled = !estado.carregando
                                ) { Text("Excluir") }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatarDataCadastro(data: String): String {
    if (data.isBlank()) return "Data não disponível"
    return try {
        val partes = data.substringBefore(".").split("T")
        if (partes.size != 2) data else {
            val dataPartes = partes[0].split("-")
            if (dataPartes.size != 3) data else "${dataPartes[2]}/${dataPartes[1]}/${dataPartes[0]} ${partes[1].take(5)}"
        }
    } catch (_: Exception) { data }
}
