package br.com.lojainteligente.frontend.ui.screens.clientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.model.ClienteRequestApi
import br.com.lojainteligente.frontend.model.ClienteResponseApi
import br.com.lojainteligente.frontend.ui.components.KoraFormPanel
import br.com.lojainteligente.frontend.viewmodel.ClienteViewModel

@Composable
fun ClienteForm(
    viewModel: ClienteViewModel,
    onClienteCadastrado: (String) -> Unit,
    cliente: ClienteResponseApi? = null
) {
    var nome by remember(cliente) { mutableStateOf(cliente?.nome.orEmpty()) }
    var email by remember(cliente) { mutableStateOf(cliente?.email.orEmpty()) }
    var telefone by remember(cliente) { mutableStateOf(cliente?.telefone.orEmpty()) }
    var ativo by remember(cliente) { mutableStateOf(cliente?.ativo ?: true) }

    val estado by viewModel.uiState.collectAsState()
    var mensagem by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }


    LaunchedEffect(cliente) {
        if (cliente == null) viewModel.limpar() else viewModel.selecionar(cliente)
    }
    LaunchedEffect(estado.mensagem) {
        if (estado.mensagem.contains("sucesso", ignoreCase = true)) {
            val feedback = estado.mensagem
            viewModel.limparMensagem()
            onClienteCadastrado(feedback)
        }
    }

    fun limparCampos() {
        nome = ""
        email = ""
        telefone = ""
        ativo = true
        viewModel.atualizarNome("")
        viewModel.atualizarEmail("")
        viewModel.atualizarTelefone("")
        viewModel.atualizarAtivo(true)
        mensagem = ""
        erro = false
    }

    KoraFormPanel(
        title = if (cliente == null) "Cadastrar cliente" else "Editar cliente",
        subtitle = "Mantenha os dados de contato organizados para suas vendas.",
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it; viewModel.atualizarNome(it) },
            label = { Text("Nome completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !estado.carregando
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; viewModel.atualizarEmail(it) },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !estado.carregando
        )

        OutlinedTextField(
            value = telefone,
            onValueChange = { telefone = it; viewModel.atualizarTelefone(it) },
            label = { Text("Telefone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !estado.carregando
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = ativo,
                onCheckedChange = { ativo = it; viewModel.atualizarAtivo(it) },
                enabled = !estado.carregando
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
                            erro = false
                            mensagem = ""
                            viewModel.salvar(ClienteRequestApi(nome.trim(), emailNormalizado, telefone.trim().ifBlank { null }, ativo))
                        }
                    }
                },
                enabled = !estado.carregando,
                modifier = Modifier.weight(if (cliente == null) 1f else 2f)
            ) {
                Text(if (estado.carregando) "Gravando..." else if (cliente == null) "Gravar" else "Salvar alterações")
            }

            if (cliente == null) {
                OutlinedButton(
                    onClick = { limparCampos() },
                    enabled = !estado.carregando,
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
                color = if (erro || (estado.mensagem.isNotBlank() && !estado.mensagem.contains("sucesso", true))) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
      }
    }
}
