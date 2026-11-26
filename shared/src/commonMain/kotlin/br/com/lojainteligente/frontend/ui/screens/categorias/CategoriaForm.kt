
package br.com.lojainteligente.frontend.ui.screens.categorias

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.viewmodel.CategoriaViewModel

@Composable
fun CategoriaForm(
    viewModel: CategoriaViewModel,
    onCategoriaCadastrada: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (estado.id == null) {
                "Cadastro de Categoria"
            } else {
                "Editar Categoria"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = estado.nome,
            onValueChange = viewModel::atualizarNome,
            label = { Text("Nome *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !estado.carregando,
            isError = estado.nome.isBlank() &&
                    estado.mensagem.isNotBlank()
        )

        OutlinedTextField(
            value = estado.descricao,
            onValueChange = viewModel::atualizarDescricao,
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            enabled = !estado.carregando
        )

        OutlinedTextField(
            value = estado.ordemExibicao,
            onValueChange = { valor ->
                if (valor.isEmpty() || valor.all { it.isDigit() }) {
                    viewModel.atualizarOrdemExibicao(valor)
                }
            },
            label = { Text("Ordem de exibição") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            enabled = !estado.carregando
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categoria ativa",
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = estado.ativo,
                onCheckedChange = viewModel::atualizarAtivo,
                enabled = !estado.carregando
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    viewModel.salvarCategoria()
                },
                enabled = !estado.carregando,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    if (estado.carregando) "Salvando..."
                    else if (estado.id == null) "Gravar"
                    else "Atualizar"
                )
            }

            OutlinedButton(
                onClick = {
                    viewModel.limparFormulario()
                },
                enabled = !estado.carregando,
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpar Campos")
            }
        }

        if (estado.mensagem.isNotBlank()) {
            Text(
                text = estado.mensagem,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    LaunchedEffect(estado.carregando, estado.mensagem) {
        if (!estado.carregando &&
            estado.mensagem == "Categoria salva com sucesso."
        ) {
            onCategoriaCadastrada()
        }
    }
}
