package br.com.lojainteligente.frontend.ui.screens.produtos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.model.ProdutoResponseApi
import br.com.lojainteligente.frontend.viewmodel.ProdutoViewModel

@Composable
fun ProdutoLista(
    viewModel: ProdutoViewModel,
    atualizarVersao: Int = 0,
    aoEditar: (ProdutoResponseApi) -> Unit
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel, atualizarVersao) {
        viewModel.carregarProdutos()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Produtos cadastrados",
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                onClick = { viewModel.carregarProdutos() }
            ) {
                Text("Atualizar")
            }
        }

        if (estado.carregando) {
            CircularProgressIndicator()
        }

        if (estado.mensagem.isNotBlank()) {
            Text(estado.mensagem)
        }

        if (estado.produtos.isEmpty() && !estado.carregando) {
            Text("Nenhum produto cadastrado.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(estado.produtos) { produto ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            produto.nome,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Código: ${produto.codigoBarras}")
                        Text("Categoria: ${produto.categoriaNome ?: "Não informada"}")
                        Text("Preço: R$ ${"%.2f".format(produto.preco)}")
                        Text("Status: ${if (produto.ativo) "Ativo" else "Inativo"}")

                        if (!produto.descricao.isNullOrBlank()) {
                            Text("Descrição: ${produto.descricao}")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.selecionarProduto(produto)
                                    aoEditar(produto)
                                }
                            ) {
                                Text("Editar")
                            }

                            TextButton(
                                onClick = {
                                    produto.id?.let {
                                        viewModel.excluirProduto(it)
                                    }
                                }
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
