
package br.com.lojainteligente.frontend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
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
    var erro by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Loja Inteligente",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text("Entre com suas credenciais para acessar o sistema.")

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                mensagem = ""
            },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = senha,
            onValueChange = {
                senha = it
                mensagem = ""
            },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                if (email.isBlank() || senha.isBlank()) {
                    mensagem = "Informe o e-mail e a senha."
                    erro = true
                } else {
                    scope.launch {
                        carregando = true
                        mensagem = ""

                        val resultado = aoEntrar(email.trim(), senha)

                        resultado.fold(
                            onSuccess = {
                                erro = false
                                mensagem = "Login realizado com sucesso."
                                aoLoginSucesso()
                            },
                            onFailure = { exception ->
                                erro = true
                                mensagem = exception.message
                                    ?: "Não foi possível realizar o login."
                            }
                        )

                        carregando = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !carregando
        ) {
            Text(if (carregando) "Entrando..." else "Entrar")
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
