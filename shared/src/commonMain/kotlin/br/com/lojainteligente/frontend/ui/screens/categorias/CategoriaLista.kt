package br.com.lojainteligente.frontend.ui.screens.categorias

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.repository.CategoriaResponseApi
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
import br.com.lojainteligente.frontend.viewmodel.CategoriaViewModel

@Composable
fun CategoriaLista(
    viewModel: CategoriaViewModel,
    modifier: Modifier = Modifier,
    atualizarVersao: Int = 0,
    aoEditar: (CategoriaResponseApi) -> Unit,
    aoNovaCategoria: () -> Unit,
    onFeedback: (String) -> Unit = {}
) {
    val estado by viewModel.uiState.collectAsState()
    var busca by remember { mutableStateOf("") }
    var categoriaParaExcluir by remember { mutableStateOf<CategoriaResponseApi?>(null) }
    val filtradas = estado.categorias.filter { categoria ->
        val termo = busca.trim()
        termo.isBlank() || listOfNotNull(
            categoria.nome,
            categoria.descricao,
            categoria.id?.toString(),
            categoria.ordemExibicao.toString()
        ).any { it.contains(termo, ignoreCase = true) }
    }

    LaunchedEffect(viewModel, atualizarVersao) { viewModel.carregarCategorias() }
    LaunchedEffect(estado.mensagem) {
        if (estado.mensagem.koraIsSuccessFeedback()) {
            val feedback = estado.mensagem
            viewModel.limparMensagem()
            onFeedback(feedback)
        }
    }

    Column(modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        KoraSectionHeader(
            title = "Categorias cadastradas",
            count = estado.categorias.size,
            addLabel = "＋ Nova categoria",
            onAdd = aoNovaCategoria,
            onRefresh = viewModel::carregarCategorias,
            refreshing = estado.carregando
        )

        KoraSearchField(busca, { busca = it }, "Buscar por nome, descrição ou ID")

        if (estado.mensagem.isNotBlank()) {
            Text(
                estado.mensagem,
                color = if (estado.mensagem.contains("sucesso", true) || estado.mensagem.contains("exclu", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }

        when {
            estado.carregando && estado.categorias.isEmpty() -> KoraLoadingState("Carregando categorias...")
            filtradas.isEmpty() && estado.categorias.isEmpty() -> KoraEmptyState("Ainda não há categorias", "Crie uma categoria para organizar os produtos da loja.")
            filtradas.isEmpty() -> KoraEmptyState("Nenhuma categoria encontrada", "Tente outro nome, descrição ou ID.")
            else -> LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtradas, key = { it.id ?: "${it.nome}-${it.ordemExibicao}" }) { categoria ->
                    KoraDataCard(contentPadding = 14.dp, contentSpacing = 7.dp) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(categoria.nome, style = MaterialTheme.typography.titleMedium)
                                Text(categoria.descricao.koraNonTechnicalOr("Sem descrição"), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        Row(Modifier.fillMaxWidth().padding(top = 2.dp), horizontalArrangement = Arrangement.End) {
                            KoraIconAction(KoraActionIcon.Edit, onClick = { viewModel.selecionarCategoria(categoria); aoEditar(categoria) })
                            KoraIconAction(KoraActionIcon.Delete, onClick = { categoriaParaExcluir = categoria }, enabled = !estado.carregando)
                        }
                    }
                }
            }
        }
    }

    categoriaParaExcluir?.let { categoria ->
        KoraConfirmationDialog(
            title = "Excluir categoria?",
            message = "A categoria “${categoria.nome}” será removida. Esta ação não pode ser desfeita.",
            onConfirm = {
                categoria.id?.let(viewModel::excluirCategoria)
                categoriaParaExcluir = null
            },
            onDismiss = { categoriaParaExcluir = null }
        )
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
