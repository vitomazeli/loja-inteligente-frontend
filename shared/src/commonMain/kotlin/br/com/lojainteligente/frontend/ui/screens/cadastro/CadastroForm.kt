package br.com.lojainteligente.frontend.ui.screens.cadastro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.lojainteligente.frontend.data.repository.ApiService
import br.com.lojainteligente.frontend.data.repository.CadastroUsuarioRequestApi
import kotlinx.coroutines.launch

@Composable
fun CadastroForm(
    apiService: ApiService,
    onCadastroSucesso: () -> Unit,
    aoIrParaLogin: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var mostrarSenha by remember { mutableStateOf(false) }
    var mostrarConfirmacao by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(max = 460.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                androidx.compose.material3.Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                    Text("K", modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text("KORA", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("GESTÃO COMERCIAL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                }
            }

            Text(
                text = "Crie sua conta",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text("Acesse o Kora para começar a organizar sua loja.", color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !carregando,
                isError = nome.isBlank() && mensagem.isNotBlank() && erro
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !carregando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = email.isNotBlank() && !email.contains("@"),
                supportingText = if (email.isNotBlank() && !email.contains("@")) ({ Text("Informe um e-mail válido.") }) else null
            )

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                visualTransformation = if (mostrarSenha) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    androidx.compose.material3.TextButton(onClick = { mostrarSenha = !mostrarSenha }) {
                        Text(if (mostrarSenha) "Ocultar" else "Mostrar")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !carregando,
                isError = senha.isNotBlank() && senha.length < 6,
                supportingText = if (senha.isNotBlank() && senha.length < 6) ({ Text("A senha deve ter ao menos 6 caracteres.") }) else null
            )

            OutlinedTextField(
                value = confirmarSenha,
                onValueChange = { confirmarSenha = it },
                label = { Text("Confirmar senha") },
                visualTransformation = if (mostrarConfirmacao) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    androidx.compose.material3.TextButton(onClick = { mostrarConfirmacao = !mostrarConfirmacao }) {
                        Text(if (mostrarConfirmacao) "Ocultar" else "Mostrar")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !carregando,
                isError = confirmarSenha.isNotBlank() && confirmarSenha != senha,
                supportingText = if (confirmarSenha.isNotBlank() && confirmarSenha != senha) ({ Text("As senhas não conferem.") }) else null
            )

            Button(
                onClick = {
                    scope.launch {
                        carregando = true
                        mensagem = ""

                        if (nome.isBlank()) {
                            mensagem = "Informe seu nome completo."
                            erro = true
                            carregando = false
                            return@launch
                        }
                        if (!email.contains("@") || !email.substringAfter("@", "").contains(".")) {
                            mensagem = "Informe um e-mail válido."
                            erro = true
                            carregando = false
                            return@launch
                        }
                        if (senha.length < 6) {
                            mensagem = "A senha deve ter ao menos 6 caracteres."
                            erro = true
                            carregando = false
                            return@launch
                        }
                        if (senha != confirmarSenha) {
                            mensagem = "As senhas não conferem."
                            erro = true
                            carregando = false
                            return@launch
                        }

                        apiService.cadastrarUsuario(
                            CadastroUsuarioRequestApi(
                                nome = nome.trim(),
                                email = email.trim(),
                                senha = senha,
                                confsenha = confirmarSenha
                            )
                        ).onSuccess { resposta ->
                            mensagem = "Cadastro realizado com sucesso. Agora você já pode entrar."
                            erro = false

                            nome = ""
                            email = ""
                            senha = ""
                            confirmarSenha = ""

                            onCadastroSucesso()
                        }.onFailure { exception ->
                            mensagem = exception.message
                                ?: "Não foi possível cadastrar o usuário."
                            erro = true
                        }

                        carregando = false
                    }
                },
                enabled = !carregando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (carregando) "Cadastrando..."
                    else "Cadastrar usuário"
                )
            }

            OutlinedButton(
                onClick = aoIrParaLogin,
                modifier = Modifier.fillMaxWidth(),
                enabled = !carregando
            ) {
                Text("Já tenho uma conta · Entrar")
            }

            if (mensagem.isNotBlank()) {
                Text(
                    text = mensagem,
                    color = if (erro) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onTertiaryContainer
                    }
                )
            }
        }
    }
    }
}
