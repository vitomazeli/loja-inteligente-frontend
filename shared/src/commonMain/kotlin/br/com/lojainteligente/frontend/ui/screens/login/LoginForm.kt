package br.com.lojainteligente.frontend.ui.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val LoginBackground = Color(0xFFF3F6FC)
private val LoginPrimary = Color(0xFF2563EB)
private val LoginText = Color(0xFF142544)
private val LoginSecondaryText = Color(0xFF647A9B)
private val LoginBorder = Color(0xFFDFE7F2)

@Composable
fun LoginForm(
    aoEntrar: suspend (String, String) -> Result<Unit>,
    aoLoginSucesso: (String) -> Unit,
    aoIrParaCadastro: () -> Unit,
    mensagemInicial: String? = null
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mostrarSenha by remember { mutableStateOf(false) }
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginBackground)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        val cardPadding = if (maxWidth < 420.dp) 24.dp else 40.dp

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 540.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, LoginBorder),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = cardPadding, vertical = cardPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = LoginPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "K",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Text(
                                text = "KORA",
                                style = MaterialTheme.typography.titleLarge,
                                color = LoginText,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "GESTÃO COMERCIAL",
                                style = MaterialTheme.typography.labelSmall,
                                color = LoginSecondaryText,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bem-vindo de volta",
                            style = MaterialTheme.typography.headlineSmall,
                            color = LoginText,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Entre para acompanhar sua loja inteligente.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoginSecondaryText
                        )
                    }

                    avisoCadastro?.let { aviso ->
                        Snackbar(
                            shape = RoundedCornerShape(12.dp),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Text(aviso, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        label = { Text("E-mail") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = email.isNotBlank() && !email.contains("@"),
                        supportingText = if (email.isNotBlank() && !email.contains("@")) {
                            { Text("Informe um e-mail válido.") }
                        } else {
                            null
                        },
                        enabled = !carregando,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LoginText,
                            unfocusedTextColor = LoginText,
                            focusedBorderColor = LoginPrimary,
                            unfocusedBorderColor = LoginBorder,
                            focusedLabelColor = LoginPrimary,
                            unfocusedLabelColor = LoginSecondaryText,
                            cursorColor = LoginPrimary
                        )
                    )

                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        label = { Text("Senha") },
                        enabled = !carregando,
                        singleLine = true,
                        visualTransformation = if (mostrarSenha) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            TextButton(onClick = { mostrarSenha = !mostrarSenha }) {
                                Text(if (mostrarSenha) "Ocultar" else "Mostrar")
                            }
                        },
                        isError = senha.isNotBlank() && senha.length < 6,
                        supportingText = if (senha.isNotBlank() && senha.length < 6) {
                            { Text("A senha deve ter ao menos 6 caracteres.") }
                        } else {
                            null
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LoginText,
                            unfocusedTextColor = LoginText,
                            focusedBorderColor = LoginPrimary,
                            unfocusedBorderColor = LoginBorder,
                            focusedLabelColor = LoginPrimary,
                            unfocusedLabelColor = LoginSecondaryText,
                            cursorColor = LoginPrimary
                        )
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
                                            mensagem = it.message ?: "Falha no login."
                                        }
                                    )
                                } catch (e: Exception) {
                                    mensagem = e.message ?: "Erro ao conectar."
                                } finally {
                                    carregando = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = !carregando && email.contains("@") && senha.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LoginPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        if (carregando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("Entrar", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (mensagem.isNotBlank()) {
                        Text(
                            text = mensagem,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    OutlinedButton(
                        onClick = aoIrParaCadastro,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = !carregando,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LoginBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = LoginText
                        )
                    ) {
                        Text("Criar uma conta", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
