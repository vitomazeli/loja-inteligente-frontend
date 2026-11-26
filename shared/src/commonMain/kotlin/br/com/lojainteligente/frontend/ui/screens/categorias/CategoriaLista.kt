
package br.com.lojainteligente.frontend.ui.screens.categorias

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.repository.CategoriaResponseApi
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

    // Carrega as categorias ao abrir a tela
    LaunchedEffect(viewModel, atualizarVersao) {
        viewModel.carregarCategorias()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Cabeçalho da listagem
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = aoNovaCategoria
            ) {
                Text("Nova categoria")
            }

            Text(
                text = "Categorias cadastradas",
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                onClick = {
                    viewModel.carregarCategorias()
                },
                enabled = !estado.carregando
            ) {
                Text("Atualizar")
            }
        }

        // Indicador de carregamento
        if (estado.carregando) {
            CircularProgressIndicator()
        }

        // Mensagem de erro ou sucesso
        if (estado.mensagem.isNotBlank()) {
            Text(
                text = estado.mensagem,
                color = if (estado.mensagem.contains("sucesso", ignoreCase = true)) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }

        // Estado vazio
        if (estado.categorias.isEmpty() && !estado.carregando) {
            Text("Nenhuma categoria cadastrada.")
        }

        // Listagem de categorias
        if (estado.categorias.isNotEmpty()) {

            Text(
                text = "Total de categorias: ${estado.categorias.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = estado.categorias,
                    key = { categoria ->
                        categoria.id
                            ?: "${categoria.nome}-${categoria.ordemExibicao}"
                    }
                ) { categoria ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {

                            Text(
                                text = categoria.nome,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = categoria.descricao ?: "Sem descrição"
                            )

                            Text("ID: ${categoria.id ?: "—"}")

                            Text(
                                "Status: ${
                                    if (categoria.ativo) "Ativa" else "Inativa"
                                }"
                            )

                            Text(
                                "Ordem de exibição: ${categoria.ordemExibicao}"
                            )

                            Text(
                                text = "Cadastrada em: ${
                                    formatarDataCadastro(categoria.dataCadastro)
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {

                                TextButton(
                                    onClick = {
                                        viewModel.selecionarCategoria(categoria)
                                        aoEditar(categoria)
                                    }
                                ) {
                                    Text("Editar")
                                }

                                TextButton(
                                    onClick = {
                                        categoria.id?.let { id ->
                                            viewModel.excluirCategoria(id)
                                        }
                                    },
                                    enabled = !estado.carregando
                                ) {
                                    Text("Excluir")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatarDataCadastro(data: String): String {
    if (data.isBlank()) {
        return "Data não disponível"
    }

    return try {
        val dataHora = data.substringBefore(".")
        val partes = dataHora.split("T")

        if (partes.size != 2) {
            data
        } else {
            val dataPartes = partes[0].split("-")

            if (dataPartes.size != 3) {
                data
            } else {
                val hora = partes[1].take(5)

                "${dataPartes[2]}/${dataPartes[1]}/${dataPartes[0]} $hora"
            }
        }
    } catch (e: Exception) {
        data
    }
}
