package br.com.lojainteligente.frontend.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginForm(
    aoEntrar: suspend (String, String) -> Result<Unit>,
    aoLoginSucesso: (String) -> Unit,
    aoIrParaCadastro: () -> Unit,
    mensagemInicial: String? = null
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }
    var avisoCadastro by remember(mensagemInicial) { mutableStateOf(mensagemInicial) }

    LaunchedEffect(mensagemInicial) {
        if (!mensagemInicial.isNullOrBlank()) {
            delay(4_000)
            avisoCadastro = null
        }
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
        Column(
            modifier = Modifier.padding(32.dp),
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
        Spacer(Modifier.height(6.dp))
        Text("Bem-vindo de volta", style = MaterialTheme.typography.headlineSmall)
        Text("Entre para acompanhar sua loja inteligente.", color = MaterialTheme.colorScheme.onSurfaceVariant)

        avisoCadastro?.let { aviso ->
        Snackbar(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) { Text(aviso) }
        }

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
                                aoLoginSucesso(email.trim())
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
            if (carregando) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            else Text("Entrar")
        }

        if (mensagem.isNotBlank()) Text(mensagem, color = MaterialTheme.colorScheme.error)
        OutlinedButton(onClick = aoIrParaCadastro, modifier = Modifier.fillMaxWidth()) {
            Text("Criar uma conta")
        }
        }
        }
    }
}
