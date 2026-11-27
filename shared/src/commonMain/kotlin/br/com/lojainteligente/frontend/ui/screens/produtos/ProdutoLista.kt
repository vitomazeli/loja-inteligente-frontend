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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.model.ProdutoResponseApi
import br.com.lojainteligente.frontend.ui.components.KoraActionIcon
import br.com.lojainteligente.frontend.ui.components.KoraConfirmationDialog
import br.com.lojainteligente.frontend.ui.components.KoraDataCard
import br.com.lojainteligente.frontend.ui.components.KoraEmptyState
import br.com.lojainteligente.frontend.ui.components.KoraIconAction
import br.com.lojainteligente.frontend.ui.components.KoraLoadingState
import br.com.lojainteligente.frontend.ui.components.KoraSearchField
import br.com.lojainteligente.frontend.ui.components.KoraSectionHeader
import br.com.lojainteligente.frontend.ui.components.KoraStatusBadge
import br.com.lojainteligente.frontend.ui.components.koraIsSuccessFeedback
import br.com.lojainteligente.frontend.ui.components.koraNonTechnicalOr
import br.com.lojainteligente.frontend.ui.components.koraFormatarMoeda
import br.com.lojainteligente.frontend.viewmodel.ProdutoViewModel

@Composable
fun ProdutoLista(
    viewModel: ProdutoViewModel,
    atualizarVersao: Int = 0,
    aoEditar: (ProdutoResponseApi) -> Unit,
    aoNovoProduto: () -> Unit,
    onFeedback: (String) -> Unit = {}
) {
    val estado by viewModel.uiState.collectAsState()
    var busca by remember { mutableStateOf("") }
    var produtoParaExcluir by remember { mutableStateOf<ProdutoResponseApi?>(null) }
    val filtrados = estado.produtos.filter { produto ->
        val termo = busca.trim()
        termo.isBlank() || listOfNotNull(
            produto.nome,
            produto.codigoBarras,
            produto.categoriaNome,
            produto.descricao,
            produto.id?.toString()
        ).any { it.contains(termo, ignoreCase = true) }
    }

    LaunchedEffect(viewModel, atualizarVersao) { viewModel.carregarProdutos() }
    LaunchedEffect(estado.mensagem) {
        if (estado.mensagem.koraIsSuccessFeedback()) {
            val feedback = estado.mensagem
            viewModel.limparMensagem()
            onFeedback(feedback)
        }
    }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        KoraSectionHeader(
            title = "Produtos cadastrados",
            count = estado.produtos.size,
            addLabel = "＋ Novo produto",
            onAdd = aoNovoProduto,
            onRefresh = viewModel::carregarProdutos,
            refreshing = estado.carregando
        )
        KoraSearchField(busca, { busca = it }, "Buscar por nome, código ou categoria")

        if (estado.mensagem.isNotBlank()) {
            Text(
                estado.mensagem,
                color = if (estado.mensagem.contains("sucesso", true) || estado.mensagem.contains("exclu", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }

        when {
            estado.carregando && estado.produtos.isEmpty() -> KoraLoadingState("Carregando produtos...")
            filtrados.isEmpty() && estado.produtos.isEmpty() -> KoraEmptyState("Ainda não há produtos", "Cadastre o primeiro produto e associe-o a uma categoria.")
            filtrados.isEmpty() -> KoraEmptyState("Nenhum produto encontrado", "Tente outro nome, código, categoria ou descrição.")
            else -> LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtrados, key = { it.id ?: it.codigoBarras }) { produto ->
                    KoraDataCard(contentPadding = 14.dp, contentSpacing = 7.dp) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(produto.nome.koraNonTechnicalOr("Produto sem nome"), modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            KoraStatusBadge(produto.ativo)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(50)) {
                                Text(
                                    produto.categoriaNome.koraNonTechnicalOr("Sem categoria"),
                                    Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(koraFormatarMoeda(produto.preco), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Text(produto.descricao.koraNonTechnicalOr("Sem descrição"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(
                            "Código: ${produto.codigoBarras.koraNonTechnicalOr("Não informado")} · ID: ${produto.id ?: "—"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            KoraIconAction(KoraActionIcon.Edit, onClick = { viewModel.selecionarProduto(produto); aoEditar(produto) })
                            KoraIconAction(KoraActionIcon.Delete, onClick = { produtoParaExcluir = produto }, enabled = !estado.carregando)
                        }
                    }
                }
            }
        }
    }

    produtoParaExcluir?.let { produto ->
        KoraConfirmationDialog(
            title = "Excluir produto?",
            message = "O produto “${produto.nome}” será removido. Esta ação não pode ser desfeita.",
            onConfirm = {
                produto.id?.let(viewModel::excluirProduto)
                produtoParaExcluir = null
            },
            onDismiss = { produtoParaExcluir = null }
        )
    }
}
