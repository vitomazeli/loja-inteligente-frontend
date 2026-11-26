package br.com.lojainteligente.frontend.ui.screens.produtos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.repository.ApiService
import br.com.lojainteligente.frontend.data.repository.CategoriaResponseApi
import br.com.lojainteligente.frontend.viewmodel.ProdutoViewModel

@Composable
fun ProdutoForm(
    apiService: ApiService,
    viewModel: ProdutoViewModel,
    onProdutoCadastrado: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()

    var categorias by remember {
        mutableStateOf<List<CategoriaResponseApi>>(emptyList())
    }

    var categoriaSelecionada by remember {
        mutableStateOf<CategoriaResponseApi?>(null)
    }

    var menuAberto by remember { mutableStateOf(false) }

    LaunchedEffect(apiService) {
        apiService.listarCategorias()
            .onSuccess { categorias = it }
    }

    LaunchedEffect(estado.categoriaId, categorias) {
        categoriaSelecionada = categorias.find {
            it.id?.toString() == estado.categoriaId
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (estado.id == null) "Cadastrar produto"
            else "Editar produto",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = estado.nome,
            onValueChange = viewModel::atualizarNome,
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !estado.carregando
        )

        OutlinedTextField(
            value = estado.descricao,
            onValueChange = viewModel::atualizarDescricao,
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estado.carregando
        )

        OutlinedTextField(
            value = estado.codigoBarras,
            onValueChange = viewModel::atualizarCodigoBarras,
            label = { Text("Código de barras") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !estado.carregando
        )

        OutlinedTextField(
            value = estado.preco,
            onValueChange = viewModel::atualizarPreco,
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !estado.carregando
        )

        Column {
            Button(
                onClick = { menuAberto = true },
                enabled = !estado.carregando
            ) {
                Text(categoriaSelecionada?.nome ?: "Selecionar categoria")
            }

            DropdownMenu(
                expanded = menuAberto,
                onDismissRequest = { menuAberto = false }
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria.nome) },
                        onClick = {
                            categoriaSelecionada = categoria
                            categoria.id?.let {
                                viewModel.atualizarCategoriaId(it.toString())
                            }
                            menuAberto = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = estado.ativo,
                onCheckedChange = viewModel::atualizarAtivo,
                enabled = !estado.carregando
            )

            Text(
                text = "Produto ativo",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    viewModel.salvarProduto()
                    onProdutoCadastrado()
                },
                enabled = !estado.carregando,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (estado.id == null) "Gravar" else "Atualizar")
            }

            OutlinedButton(
                onClick = {
                    viewModel.limparFormulario()
                    categoriaSelecionada = null
                },
                enabled = !estado.carregando,
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpar")
            }
        }

        if (estado.mensagem.isNotBlank()) {
            Text(
                text = estado.mensagem,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
