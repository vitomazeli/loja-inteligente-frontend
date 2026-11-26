package br.com.lojainteligente.frontend.ui.screens.vendas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.repository.ApiService
import br.com.lojainteligente.frontend.model.ClienteResponseApi
import br.com.lojainteligente.frontend.data.repository.CategoriaResponseApi
import br.com.lojainteligente.frontend.model.ProdutoResponseApi
import br.com.lojainteligente.frontend.model.ItemVendaRequestApi
import br.com.lojainteligente.frontend.model.VendaRequestApi
import br.com.lojainteligente.frontend.model.VendaResponseApi
import br.com.lojainteligente.frontend.ui.components.KoraFormPanel
import br.com.lojainteligente.frontend.viewmodel.VendaViewModel
import kotlinx.coroutines.launch

@Composable
fun VendaForm(
    apiService: ApiService,
    viewModel: VendaViewModel,
    onVendaCadastrada: (String) -> Unit,
    vendaInicial: VendaResponseApi? = null
) {
    var clientes by remember {
        mutableStateOf<List<ClienteResponseApi>>(emptyList())
    }

    var produtos by remember {
        mutableStateOf<List<ProdutoResponseApi>>(emptyList())
    }
    var categorias by remember { mutableStateOf<List<CategoriaResponseApi>>(emptyList()) }

    var clienteSelecionado by remember(vendaInicial) {
        mutableStateOf<ClienteResponseApi?>(null)
    }

    var produtoSelecionado by remember(vendaInicial) {
        mutableStateOf<ProdutoResponseApi?>(null)
    }
    var categoriaSelecionada by remember(vendaInicial) { mutableStateOf<CategoriaResponseApi?>(null) }

    var quantidade by remember { mutableStateOf("1") }
    var desconto by remember { mutableStateOf("0") }
    var observacao by remember(vendaInicial) {
        mutableStateOf(vendaInicial?.observacao?.takeUnless { it.trim().equals("string", ignoreCase = true) }.orEmpty())
    }

    var menuClientesAberto by remember { mutableStateOf(false) }
    var menuCategoriasAberto by remember { mutableStateOf(false) }
    var menuProdutosAberto by remember { mutableStateOf(false) }

    var mensagem by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }
    val estado by viewModel.uiState.collectAsState()

    val itens = remember(vendaInicial) {
        mutableStateListOf<ItemVendaRequestApi>()
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(apiService, vendaInicial) {
        viewModel.limparFormulario()
        vendaInicial?.let { inicial ->
            viewModel.selecionarCliente(inicial.clienteId)
            viewModel.atualizarObservacao(
                inicial.observacao?.takeUnless { it.trim().equals("string", ignoreCase = true) }.orEmpty()
            )
            viewModel.atualizarItens(inicial.itens.mapNotNull { item ->
                item.produtoId?.let { ItemVendaRequestApi(it, item.quantidade, item.desconto) }
            })
        }
        apiService.listarClientes()
            .onSuccess {
                clientes = it
                clienteSelecionado = vendaInicial?.clienteId?.let { id -> it.find { cliente -> cliente.id == id } }
            }
            .onFailure {
                mensagem = "Erro ao carregar clientes: ${it.message}"
            }

        apiService.listarCategorias()
            .onSuccess { categorias = it }
            .onFailure { mensagem = "Erro ao carregar categorias: ${it.message}" }

        apiService.listarProdutos()
            .onSuccess { listaProdutos ->
                produtos = listaProdutos
                val categoriaDaVenda = vendaInicial?.itens
                    ?.firstNotNullOfOrNull { item ->
                        val idCategoria = listaProdutos.firstOrNull { produto -> produto.id == item.produtoId }?.categoriaId
                        categorias.firstOrNull { categoria -> categoria.id == idCategoria }
                    }
                categoriaSelecionada = categoriaDaVenda
                vendaInicial?.itens?.forEach { item ->
                    val produtoId = item.produtoId
                    if (produtoId != null) itens.add(ItemVendaRequestApi(produtoId, item.quantidade, item.desconto))
                }
            }
            .onFailure {
                mensagem = "Erro ao carregar produtos: ${it.message}"
            }
    }
    LaunchedEffect(estado.mensagem) {
        if (estado.mensagem.contains("sucesso", ignoreCase = true)) {
            val feedback = estado.mensagem
            viewModel.limparMensagem()
            onVendaCadastrada(feedback)
        }
    }

    KoraFormPanel(
        title = if (vendaInicial == null) "Cadastrar venda" else "Editar venda · ID ${vendaInicial.id}",
        subtitle = "Selecione o cliente e adicione os produtos da venda.",
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

        // Selecionar cliente
        Column {
            FilledTonalButton(
                onClick = { menuClientesAberto = true },
                enabled = !carregando && vendaInicial == null
            ) {
                Text(clienteSelecionado?.nome ?: "Selecionar cliente")
            }

            DropdownMenu(
                expanded = menuClientesAberto,
                onDismissRequest = { menuClientesAberto = false },
                containerColor = Color(0xFFF3F8FF),
                tonalElevation = 0.dp
            ) {
                clientes.forEach { cliente ->
                    DropdownMenuItem(
                        text = { Text(cliente.nome) },
                        onClick = {
                            clienteSelecionado = cliente
                            viewModel.selecionarCliente(cliente.id)
                            menuClientesAberto = false
                            mensagem = ""
                        }
                    )
                }
            }
        }

        // Categoria deve ser selecionada antes do produto.
        Column {
            FilledTonalButton(
                onClick = { menuCategoriasAberto = true },
                enabled = !carregando
            ) { Text(categoriaSelecionada?.nome ?: "Selecionar categoria") }

            DropdownMenu(
                expanded = menuCategoriasAberto,
                onDismissRequest = { menuCategoriasAberto = false },
                containerColor = Color(0xFFF3F8FF),
                tonalElevation = 0.dp
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria.nome) },
                        onClick = {
                            categoriaSelecionada = categoria
                            produtoSelecionado = null
                            menuCategoriasAberto = false
                            mensagem = ""
                        }
                    )
                }
            }
        }

        // Selecionar produto
        Column {
            FilledTonalButton(
                onClick = { menuProdutosAberto = true },
                enabled = !carregando && categoriaSelecionada != null
            ) {
                Text(
                    produtoSelecionado?.let {
                        "${it.nome} - R$ ${it.preco}"
                    } ?: "Selecionar produto"
                )
            }

            DropdownMenu(
                expanded = menuProdutosAberto,
                onDismissRequest = { menuProdutosAberto = false },
                containerColor = Color(0xFFF3F8FF),
                tonalElevation = 0.dp
            ) {
                produtos.filter { it.categoriaId == categoriaSelecionada?.id }.forEach { produto ->
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
            if (categoriaSelecionada != null && produtos.none { it.categoriaId == categoriaSelecionada?.id }) {
                Text("Não há produtos cadastrados nesta categoria.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        mensagem = "Selecione uma categoria e um produto válido."
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
                        viewModel.atualizarItens(itens.toList())

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
                            viewModel.atualizarItens(itens.toList())
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
            onValueChange = { observacao = it; viewModel.atualizarObservacao(it) },
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
                                    val request = VendaRequestApi(
                                        clienteId = clienteId,
                                        itens = itens.toList(),
                                        observacao = observacao.trim().ifBlank { null }
                                    )
                                    viewModel.selecionarCliente(clienteId)
                                    viewModel.atualizarItens(itens.toList())
                                    viewModel.atualizarObservacao(observacao)
                                    viewModel.salvar(vendaInicial?.id, request).join()
                                } finally {
                                    carregando = false
                                }
                            }
                        }
                    }
                },
                enabled = !carregando && !estado.carregando,
                modifier = Modifier.weight(if (vendaInicial == null) 1f else 2f)
            ) {
                Text(
                    if (carregando || estado.carregando) "Salvando..."
                    else if (vendaInicial == null) "Finalizar venda" else "Salvar alterações"
                )
            }

            if (vendaInicial == null) {
                OutlinedButton(
                    onClick = {
                        quantidade = "1"
                        desconto = "0"
                        observacao = ""
                        viewModel.atualizarObservacao("")
                        mensagem = "Quantidade, desconto e observação limpos."
                    },
                    enabled = !carregando && !estado.carregando,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Limpar Campos")
                }
            }
        }

        val mensagemExibida = mensagem.ifBlank { estado.mensagem }
        if (mensagemExibida.isNotBlank()) {
            Text(
                text = mensagemExibida,
                color = if (
                    mensagem == "Venda cadastrada com sucesso!" ||
                    estado.mensagem == "Venda salva com sucesso." ||
                    mensagem.startsWith("Quantidade, desconto e observação limpos") ||
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
}
