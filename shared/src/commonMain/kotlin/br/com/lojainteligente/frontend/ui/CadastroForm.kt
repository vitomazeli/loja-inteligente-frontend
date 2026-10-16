
package br.com.lojainteligente.frontend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.ApiService
import br.com.lojainteligente.frontend.data.CadastroUsuarioRequestApi
import kotlinx.coroutines.launch

@Composable
fun CadastroForm(
    apiService: ApiService,
    onCadastroSucesso: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Cadastrar usuário",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    scope.launch {
                        carregando = true
                        mensagem = ""

                        apiService.cadastrarUsuario(
                            CadastroUsuarioRequestApi(
                                nome = nome.trim(),
                                email = email.trim(),
                                senha = senha
                            )
                        ).onSuccess { resposta ->
                            mensagem = resposta
                            erro = false

                            nome = ""
                            email = ""
                            senha = ""

                            onCadastroSucesso()
                        }.onFailure { exception ->
                            mensagem = exception.message
                                ?: "Não foi possível cadastrar o usuário."
                            erro = true
                        }

                        carregando = false
                    }
                },
                enabled = !carregando &&
                        nome.isNotBlank() &&
                        email.isNotBlank() &&
                        senha.length >= 6,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (carregando) "Cadastrando..."
                    else "Cadastrar usuário"
                )
            }

            if (mensagem.isNotBlank()) {
                Text(
                    text = mensagem,
                    color = if (erro) {
                        MaterialTheme.colorScheme.error
                    } else {
                        Color(0xFF15803D)
                    }
                )
            }
        }
    }
}
