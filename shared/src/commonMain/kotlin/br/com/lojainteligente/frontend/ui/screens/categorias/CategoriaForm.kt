
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
import br.com.lojainteligente.frontend.ui.components.KoraFormPanel
import br.com.lojainteligente.frontend.viewmodel.CategoriaViewModel

@Composable
fun CategoriaForm(
    viewModel: CategoriaViewModel,
    onCategoriaCadastrada: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.uiState.collectAsState()

    KoraFormPanel(
        title = if (estado.id == null) {
                "Cadastro de Categoria"
            } else {
                "Editar Categoria"
            },
        subtitle = "Organize os produtos e defina a ordem de exibição no catálogo.",
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

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
                if (valor.length <= 9 && valor.all { it in '0'..'9' }) {
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
                modifier = Modifier.weight(if (estado.id == null) 1f else 2f)
            ) {
                Text(
                    if (estado.carregando) "Salvando..."
                    else if (estado.id == null) "Gravar"
                    else "Atualizar"
                )
            }

            if (estado.id == null) {
                OutlinedButton(
                    onClick = { viewModel.limparFormulario() },
                    enabled = !estado.carregando,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Limpar Campos")
                }
            }
        }

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
      }
    }

    LaunchedEffect(estado.carregando, estado.mensagem) {
        if (!estado.carregando &&
            estado.mensagem.contains("sucesso", ignoreCase = true)
        ) {
            val feedback = estado.mensagem
            viewModel.limparMensagem()
            onCategoriaCadastrada(feedback)
        }
    }
}
