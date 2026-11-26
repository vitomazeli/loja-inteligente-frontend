package br.com.lojainteligente.frontend.ui.screens.produtos

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
import br.com.lojainteligente.frontend.model.ProdutoResponseApi
import br.com.lojainteligente.frontend.ui.components.KoraEmptyState
import br.com.lojainteligente.frontend.ui.components.KoraSectionHeader
import br.com.lojainteligente.frontend.ui.components.KoraStatusBadge
import br.com.lojainteligente.frontend.viewmodel.ProdutoViewModel

@Composable
fun ProdutoLista(
    viewModel: ProdutoViewModel,
    atualizarVersao: Int = 0,
    aoEditar: (ProdutoResponseApi) -> Unit,
    aoNovoProduto: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel, atualizarVersao) { viewModel.carregarProdutos() }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        KoraSectionHeader(
            title = "Produtos cadastrados",
            count = estado.produtos.size,
            addLabel = "＋ Novo produto",
            onAdd = aoNovoProduto,
            onRefresh = viewModel::carregarProdutos,
            refreshing = estado.carregando
        )
        if (estado.carregando && estado.produtos.isEmpty()) CircularProgressIndicator()
        if (estado.mensagem.isNotBlank()) {
            Text(
                estado.mensagem,
                color = if (estado.mensagem.contains("sucesso", true) || estado.mensagem.contains("exclu", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
        if (!estado.carregando && estado.produtos.isEmpty() && estado.mensagem.isBlank()) {
            KoraEmptyState("Ainda não há produtos", "Cadastre o primeiro produto e associe-o a uma categoria.")
        }
        if (estado.produtos.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.produtos, key = { it.id ?: it.codigoBarras }) { produto ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(produto.nome, style = MaterialTheme.typography.titleMedium)
                                    Text("${produto.categoriaNome ?: "Sem categoria"} · Código ${produto.codigoBarras}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                KoraStatusBadge(produto.ativo)
                            }
                            Text(
                                "R$ ${"%.2f".format(produto.preco)}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (!produto.descricao.isNullOrBlank()) {
                                Text(produto.descricao, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { viewModel.selecionarProduto(produto); aoEditar(produto) }) { Text("✎  Editar") }
                                TextButton(onClick = { produto.id?.let(viewModel::excluirProduto) }, enabled = !estado.carregando) { Text("Excluir") }
                            }
                        }
                    }
                }
            }
        }
    }
}
