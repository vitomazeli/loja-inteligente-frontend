
package br.com.lojainteligente.frontend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.ApiService
import br.com.lojainteligente.frontend.data.CategoriaResponseApi
import br.com.lojainteligente.frontend.model.ProdutoRequestApi
import kotlinx.coroutines.launch

@Composable
fun ProdutoForm(
    apiService: ApiService,
    onProdutoCadastrado: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var codigoBarras by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var ativo by remember { mutableStateOf(true) }

    var categorias by remember {
        mutableStateOf<List<CategoriaResponseApi>>(emptyList())
    }
    var categoriaSelecionada by remember {
        mutableStateOf<CategoriaResponseApi?>(null)
    }
    var menuAberto by remember { mutableStateOf(false) }

    var mensagem by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun limparCampos() {
        nome = ""
        descricao = ""
        codigoBarras = ""
        preco = ""
        ativo = true
        categoriaSelecionada = null
        mensagem = ""
        erro = false
    }

    LaunchedEffect(apiService) {
        apiService.listarCategorias()
            .onSuccess {
                categorias = it
            }
            .onFailure {
                mensagem = "Erro ao carregar categorias: ${it.message}"
                erro = true
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Cadastrar produto")

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !carregando
        )

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !carregando
        )

        OutlinedTextField(
            value = codigoBarras,
            onValueChange = { codigoBarras = it },
            label = { Text("Código de barras") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !carregando
        )

        OutlinedTextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !carregando
        )

        Column {
            Button(
                onClick = { menuAberto = true },
                enabled = !carregando
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
                checked = ativo,
                onCheckedChange = { ativo = it },
                enabled = !carregando
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
                    val precoConvertido = preco.trim()
                        .replace(",", ".")
                        .toDoubleOrNull()

                    when {
                        nome.isBlank() -> {
                            mensagem = "Informe o nome do produto."
                            erro = true
                        }

                        nome.trim().length > 100 -> {
                            mensagem = "O nome deve ter no máximo 100 caracteres."
                            erro = true
                        }

                        codigoBarras.isBlank() -> {
                            mensagem = "Informe o código de barras."
                            erro = true
                        }

                        codigoBarras.trim().length > 50 -> {
                            mensagem = "O código de barras deve ter no máximo 50 caracteres."
                            erro = true
                        }

                        descricao.length > 255 -> {
                            mensagem = "A descrição deve ter no máximo 255 caracteres."
                            erro = true
                        }

                        precoConvertido == null || precoConvertido <= 0.0 -> {
                            mensagem = "Informe um preço maior que zero."
                            erro = true
                        }

                        categoriaSelecionada?.id == null -> {
                            mensagem = "Selecione uma categoria válida."
                            erro = true
                        }

                        else -> {
                            carregando = true
                            mensagem = ""
                            erro = false

                            scope.launch {
                                apiService.cadastrarProduto(
                                    ProdutoRequestApi(
                                        nome = nome.trim(),
                                        descricao = descricao.trim()
                                            .ifBlank { null },
                                        codigoBarras = codigoBarras.trim(),
                                        preco = precoConvertido,
                                        categoriaId = categoriaSelecionada!!.id!!,
                                        ativo = ativo
                                    )
                                ).onSuccess {
                                    limparCampos()
                                    mensagem = "Produto cadastrado com sucesso!"
                                    onProdutoCadastrado()
                                }.onFailure {
                                    mensagem =
                                        "Erro ao cadastrar produto: ${it.message}"
                                    erro = true
                                }

                                carregando = false
                            }
                        }
                    }
                },
                enabled = !carregando,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (carregando) "Gravando..." else "Gravar")
            }

            OutlinedButton(
                onClick = { limparCampos() },
                enabled = !carregando,
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpar Campos")
            }
        }

        if (mensagem.isNotBlank()) {
            Text(
                text = mensagem,
                color = if (erro) {
                    androidx.compose.material3.MaterialTheme.colorScheme.error
                } else {
                    androidx.compose.material3.MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}
