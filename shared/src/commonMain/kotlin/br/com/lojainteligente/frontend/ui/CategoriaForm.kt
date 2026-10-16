
package br.com.lojainteligente.frontend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.model.Categoria
import kotlinx.coroutines.launch

@Composable
fun CategoriaForm(
    aoGravar: suspend (Categoria) -> Result<String>
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var ativo by remember { mutableStateOf(true) }
    var ordemExibicao by remember { mutableStateOf("0") }
    var mensagem by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Cadastro de Categoria",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = nome,
            onValueChange = { novoNome ->
                nome = novoNome
                mensagem = ""
                erro = false
            },
            label = { Text("Nome *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = erro && nome.isBlank(),
            enabled = !carregando
        )

        OutlinedTextField(
            value = descricao,
            onValueChange = { novaDescricao ->
                descricao = novaDescricao
            },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            enabled = !carregando
        )

        OutlinedTextField(
            value = ordemExibicao,
            onValueChange = { novoValor ->
                if (novoValor.isEmpty() || novoValor.all { it.isDigit() }) {
                    ordemExibicao = novoValor
                }
            },
            label = { Text("Ordem de exibição") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            enabled = !carregando
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
                checked = ativo,
                onCheckedChange = { novoEstado ->
                    ativo = novoEstado
                },
                enabled = !carregando
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    mensagem = ""
                    erro = false

                    when {
                        nome.isBlank() -> {
                            mensagem = "Informe o nome da categoria."
                            erro = true
                        }

                        ordemExibicao.toIntOrNull() == null -> {
                            mensagem = "Informe uma ordem de exibição válida."
                            erro = true
                        }

                        else -> {
                            val categoria = Categoria(
                                nome = nome.trim(),
                                descricao = descricao.trim().ifBlank { null },
                                ativo = ativo,
                                ordemExibicao = ordemExibicao.toInt()
                            )

                            scope.launch {
                                carregando = true

                                try {
                                    aoGravar(categoria).fold(
                                        onSuccess = { resposta ->
                                            mensagem = resposta
                                            erro = false
                                        },
                                        onFailure = { excecao ->
                                            mensagem = excecao.message
                                                ?: "Não foi possível salvar a categoria."
                                            erro = true
                                        }
                                    )
                                } catch (excecao: Exception) {
                                    mensagem = excecao.message
                                        ?: "Não foi possível conectar ao servidor."
                                    erro = true
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
                    text = if (carregando) "Salvando..." else "Gravar"
                )
            }

            OutlinedButton(
                onClick = {
                    nome = ""
                    descricao = ""
                    ativo = true
                    ordemExibicao = "0"
                    mensagem = ""
                    erro = false
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
                color = if (erro) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}
