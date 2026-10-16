
package br.com.lojainteligente.frontend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.ApiService
import br.com.lojainteligente.frontend.model.ClienteResponseApi
import br.com.lojainteligente.frontend.model.ProdutoResponseApi
import br.com.lojainteligente.frontend.model.ItemVendaRequestApi
import br.com.lojainteligente.frontend.model.VendaRequestApi
import kotlinx.coroutines.launch

@Composable
fun VendaForm(
    apiService: ApiService,
    onVendaCadastrada: () -> Unit
) {
    var clientes by remember {
        mutableStateOf<List<ClienteResponseApi>>(emptyList())
    }

    var produtos by remember {
        mutableStateOf<List<ProdutoResponseApi>>(emptyList())
    }

    var clienteSelecionado by remember {
        mutableStateOf<ClienteResponseApi?>(null)
    }

    var produtoSelecionado by remember {
        mutableStateOf<ProdutoResponseApi?>(null)
    }

    var quantidade by remember { mutableStateOf("1") }
    var desconto by remember { mutableStateOf("0") }
    var observacao by remember { mutableStateOf("") }

    var menuClientesAberto by remember { mutableStateOf(false) }
    var menuProdutosAberto by remember { mutableStateOf(false) }

    var mensagem by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

    val itens = remember {
        mutableStateListOf<ItemVendaRequestApi>()
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(apiService) {
        apiService.listarClientes()
            .onSuccess {
                clientes = it
            }
            .onFailure {
                mensagem = "Erro ao carregar clientes: ${it.message}"
            }

        apiService.listarProdutos()
            .onSuccess {
                produtos = it
            }
            .onFailure {
                mensagem = "Erro ao carregar produtos: ${it.message}"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Cadastrar venda",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Selecionar cliente
        Column {
            Button(
                onClick = { menuClientesAberto = true },
                enabled = !carregando
            ) {
                Text(clienteSelecionado?.nome ?: "Selecionar cliente")
            }

            DropdownMenu(
                expanded = menuClientesAberto,
                onDismissRequest = { menuClientesAberto = false }
            ) {
                clientes.forEach { cliente ->
                    DropdownMenuItem(
                        text = { Text(cliente.nome) },
                        onClick = {
                            clienteSelecionado = cliente
                            menuClientesAberto = false
                            mensagem = ""
                        }
                    )
                }
            }
        }

        // Selecionar produto
        Column {
            Button(
                onClick = { menuProdutosAberto = true },
                enabled = !carregando
            ) {
                Text(
                    produtoSelecionado?.let {
                        "${it.nome} - R$ ${it.preco}"
                    } ?: "Selecionar produto"
                )
            }

            DropdownMenu(
                expanded = menuProdutosAberto,
                onDismissRequest = { menuProdutosAberto = false }
            ) {
                produtos.forEach { produto ->
                    DropdownMenuItem(
                        text = {
                            Text("${produto.nome} - R$ ${produto.preco}")
                        },
                        onClick = {
                            produtoSelecionado = produto
                            menuProdutosAberto = false
                            mensagem = ""
                        }
                    )
                }
            }
        }

        // Quantidade
        OutlinedTextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantidade") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !carregando
        )

        // Desconto
        OutlinedTextField(
            value = desconto,
            onValueChange = { desconto = it },
            label = { Text("Desconto (R$)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !carregando
        )

        // Adicionar produto à lista de itens
        Button(
            onClick = {
                val produto = produtoSelecionado
                val produtoId = produto?.id
                val qtd = quantidade.toIntOrNull()
                val valorDesconto = desconto.toDoubleOrNull()

                when {
                    produto == null || produtoId == null || produtoId <= 0L -> {
                        mensagem = "Selecione um produto válido."
                    }

                    qtd == null || qtd <= 0 -> {
                        mensagem = "Informe uma quantidade maior que zero."
                    }

                    valorDesconto == null ||
                            !valorDesconto.isFinite() ||
                            valorDesconto < 0.0 -> {
                        mensagem = "Informe um desconto válido."
                    }

                    else -> {
                        itens.add(
                            ItemVendaRequestApi(
                                produtoId = produtoId,
                                quantidade = qtd,
                                desconto = valorDesconto
                            )
                        )

                        produtoSelecionado = null
                        quantidade = "1"
                        desconto = "0"
                        mensagem = "Produto adicionado à venda!"
                    }
                }
            },
            enabled = !carregando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar produto")
        }

        HorizontalDivider()

        Text(
            text = "Itens da venda (${itens.size})",
            style = MaterialTheme.typography.titleMedium
        )

        if (itens.isEmpty()) {
            Text("Nenhum produto adicionado.")
        } else {
            itens.forEachIndexed { index, item ->
                val produto = produtos.find {
                    it.id == item.produtoId
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(produto?.nome ?: "Produto ${item.produtoId}")
                        Text("Quantidade: ${item.quantidade}")
                        Text("Desconto: R$ ${item.desconto}")
                    }

                    OutlinedButton(
                        onClick = {
                            itens.removeAt(index)
                            mensagem = ""
                        },
                        enabled = !carregando
                    ) {
                        Text("Remover")
                    }
                }

                HorizontalDivider()
            }
        }

        // Observação opcional
        OutlinedTextField(
            value = observacao,
            onValueChange = { observacao = it },
            label = { Text("Observação (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !carregando
        )

        // Finalizar venda e limpar campos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val clienteId = clienteSelecionado?.id

                    when {
                        clienteId == null || clienteId <= 0L -> {
                            mensagem = "Selecione um cliente válido."
                        }

                        itens.isEmpty() -> {
                            mensagem = "Adicione pelo menos um produto."
                        }

                        else -> {
                            carregando = true
                            mensagem = ""

                            scope.launch {
                                try {
                                    apiService.cadastrarVenda(
                                        VendaRequestApi(
                                            clienteId = clienteId,
                                            itens = itens.toList(),
                                            observacao = observacao
                                                .trim()
                                                .ifBlank { null }
                                        )
                                    ).onSuccess {
                                        mensagem =
                                            "Venda cadastrada com sucesso!"

                                        clienteSelecionado = null
                                        produtoSelecionado = null
                                        itens.clear()
                                        observacao = ""
                                        quantidade = "1"
                                        desconto = "0"

                                        onVendaCadastrada()
                                    }.onFailure {
                                        mensagem =
                                            "Erro ao cadastrar venda: ${it.message}"
                                    }
                                } catch (e: Exception) {
                                    mensagem =
                                        "Erro ao cadastrar venda: ${e.message}"
                                } finally {
                                    carregando = false
                                }
                            }
                        }
                    }
                },
                enabled = !carregando,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    if (carregando) "Registrando..." else "Finalizar venda"
                )
            }

            OutlinedButton(
                onClick = {
                    clienteSelecionado = null
                    produtoSelecionado = null
                    quantidade = "1"
                    desconto = "0"
                    observacao = ""
                    itens.clear()
                    menuClientesAberto = false
                    menuProdutosAberto = false
                    mensagem = "Campos limpos."
                },
                enabled = !carregando,
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpar Campos")
            }
        }

        if (mensagem.isNotBlank()) {
            Text(
                text = mensagem,
                color = if (
                    mensagem == "Venda cadastrada com sucesso!" ||
                    mensagem == "Campos limpos." ||
                    mensagem == "Produto adicionado à venda!"
                ) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}
