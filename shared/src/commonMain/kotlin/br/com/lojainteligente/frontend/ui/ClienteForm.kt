package br.com.lojainteligente.frontend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.ApiService
import br.com.lojainteligente.frontend.model.ClienteRequestApi
import kotlinx.coroutines.launch

@Composable
fun ClienteForm(
    apiService: ApiService,
    onClienteCadastrado: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var ativo by remember { mutableStateOf(true) }

    var mensagem by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun limparCampos() {
        nome = ""
        email = ""
        telefone = ""
        ativo = true
        mensagem = ""
        erro = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Cadastrar cliente")

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !carregando
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !carregando
        )

        OutlinedTextField(
            value = telefone,
            onValueChange = { telefone = it },
            label = { Text("Telefone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !carregando
        )

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
                text = "Cliente ativo",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val emailNormalizado = email.trim()

                    when {
                        nome.isBlank() -> {
                            mensagem = "Informe o nome do cliente."
                            erro = true
                        }

                        nome.trim().length > 100 -> {
                            mensagem = "O nome deve ter no máximo 100 caracteres."
                            erro = true
                        }

                        emailNormalizado.isBlank() ||
                                !emailNormalizado.contains("@") ||
                                !emailNormalizado.substringAfter("@").contains(".") -> {
                            mensagem = "Informe um e-mail válido."
                            erro = true
                        }

                        else -> {
                            carregando = true
                            mensagem = ""
                            erro = false

                            scope.launch {
                                try {
                                    apiService.cadastrarCliente(
                                        ClienteRequestApi(
                                            nome = nome.trim(),
                                            email = emailNormalizado,
                                            telefone = telefone.trim()
                                                .ifBlank { null },
                                            ativo = ativo
                                        )
                                    ).onSuccess {
                                        limparCampos()
                                        mensagem =
                                            "Cliente cadastrado com sucesso!"
                                        onClienteCadastrado()
                                    }.onFailure {
                                        mensagem =
                                            "Erro ao cadastrar cliente: ${it.message}"
                                        erro = true
                                    }
                                } catch (e: Exception) {
                                    mensagem =
                                        "Erro ao cadastrar cliente: ${e.message}"
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
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}
