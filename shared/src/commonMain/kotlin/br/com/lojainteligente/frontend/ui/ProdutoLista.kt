
package br.com.lojainteligente.frontend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.ApiService
import br.com.lojainteligente.frontend.model.ProdutoResponseApi
import androidx.compose.material3.MaterialTheme

@Composable
fun ProdutoLista(
    apiService: ApiService,
    atualizarVersao: Int = 0
) {
    var produtos by remember { mutableStateOf<List<ProdutoResponseApi>>(emptyList()) }
    var carregando by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }

    LaunchedEffect(apiService, atualizarVersao) {
        carregando = true
        mensagem = ""

        apiService.listarProdutos()
            .onSuccess { produtos = it }
            .onFailure {
                mensagem = "Erro ao carregar produtos: ${it.message}"
            }

        carregando = false
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Produtos cadastrados")

            Button(
                onClick = {
                    // Atualiza o parâmetro quando o pai controlar a versão.
                    mensagem = "Use o botão de atualizar da tela principal."
                }
            ) {
                Text("Atualizar")
            }
        }

        if (carregando) {
            CircularProgressIndicator()
        } else if (produtos.isEmpty() && mensagem.isBlank()) {
            Text("Nenhum produto cadastrado.")
        }

        if (mensagem.isNotBlank()) {
            Text(mensagem)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(produtos) { produto ->
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
                        Text(produto.nome)
                        Text("Código: ${produto.codigoBarras}")
                        Text("Categoria: ${produto.categoriaNome}")
                        Text("Preço: R$ ${"%.2f".format(produto.preco)}")
                        Text("Status: ${if (produto.ativo) "Ativo" else "Inativo"}")

                        if (!produto.descricao.isNullOrBlank()) {
                            Text("Descrição: ${produto.descricao}")
                        }
                    }
                }
            }
        }
    }
}
