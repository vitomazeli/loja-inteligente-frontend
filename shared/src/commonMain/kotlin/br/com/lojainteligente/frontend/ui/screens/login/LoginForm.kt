package br.com.lojainteligente.frontend.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import kotlinx.coroutines.launch

@Composable
fun LoginForm(
    aoEntrar: suspend (String, String) -> Result<Unit>,
    aoLoginSucesso: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Loja Inteligente")
        Text("Entre com suas credenciais.")

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("E-mail") },
            enabled = true,
            readOnly = false,
            singleLine = true
        )

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Senha") },
            enabled = true,
            readOnly = false,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                scope.launch {
                    carregando = true
                    mensagem = ""

                    try {
                        val resultado = aoEntrar(email.trim(), senha)

                        resultado.fold(
                            onSuccess = {
                                aoLoginSucesso()
                            },
                            onFailure = {
                                mensagem = it.message
                                    ?: "Falha no login."
                            }
                        )
                    } catch (e: Exception) {
                        mensagem = e.message ?: "Erro ao conectar."
                    } finally {
                        carregando = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !carregando
        ) {
            Text(if (carregando) "Entrando..." else "Entrar")
        }

        Text(mensagem)
    }
}
