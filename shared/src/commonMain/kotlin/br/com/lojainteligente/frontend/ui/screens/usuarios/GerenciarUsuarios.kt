package br.com.lojainteligente.frontend.ui.screens.usuarios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.BorderStroke
import br.com.lojainteligente.frontend.ui.components.KoraEmptyState
import br.com.lojainteligente.frontend.ui.components.KoraStatusBadge
import br.com.lojainteligente.frontend.data.repository.ApiService
import br.com.lojainteligente.frontend.data.repository.CadastroUsuarioResponseApi
import kotlinx.coroutines.launch

@Composable
fun GerenciarUsuarios(apiService: ApiService) {
    var vendedores by remember { mutableStateOf<List<CadastroUsuarioResponseApi>>(emptyList()) }
    var carregando by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf<CadastroUsuarioResponseApi?>(null) }
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun carregar() {
        scope.launch {
            carregando = true
            apiService.listarUsuarios()
                .onSuccess { todos -> vendedores = todos.filter { it.perfil.equals("VENDEDOR", true) } }
                .onFailure { mensagem = it.message ?: "Não foi possível carregar os vendedores." }
            carregando = false
        }
    }

    LaunchedEffect(apiService) { carregar() }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Equipe de vendas", style = MaterialTheme.typography.titleLarge)
                Text("${vendedores.size} usuários vendedores", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            OutlinedButton(onClick = ::carregar, enabled = !carregando) { Text("Atualizar") }
        }
        if (carregando) CircularProgressIndicator()
        if (mensagem.isNotBlank()) Text(mensagem, color = MaterialTheme.colorScheme.error)
        if (!carregando && vendedores.isEmpty() && mensagem.isBlank()) {
            KoraEmptyState("Nenhum vendedor cadastrado", "As contas com perfil vendedor aparecerão aqui.")
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(vendedores, key = { it.id ?: it.email }) { vendedor ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(vendedor.nome, style = MaterialTheme.typography.titleMedium)
                                Text(vendedor.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            KoraStatusBadge(vendedor.ativo, "Ativo", "Desativado")
                        }
                        Text("Perfil: ${vendedor.perfil} · ID ${vendedor.id ?: "—"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = {
                                editando = vendedor
                                nome = vendedor.nome
                                email = vendedor.email
                                mensagem = ""
                            }) { Text("Editar") }
                            TextButton(onClick = {
                                val id = vendedor.id ?: return@TextButton
                                scope.launch {
                                    carregando = true
                                    apiService.atualizarStatusVendedor(vendedor, ativo = !vendedor.ativo)
                                        .onSuccess {
                                            mensagem = if (vendedor.ativo) "Vendedor desativado." else "Vendedor ativado."
                                            carregar()
                                        }
                                        .onFailure { mensagem = it.message ?: "Não foi possível atualizar o status do vendedor." }
                                    carregando = false
                                }
                            }, enabled = !carregando) { Text(if (vendedor.ativo) "Desativar" else "Ativar") }
                        }
                    }
                }
            }
        }
    }

    editando?.let { vendedor ->
        AlertDialog(
            onDismissRequest = { editando = null },
            title = { Text("Editar vendedor") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(nome, { nome = it }, label = { Text("Nome") }, singleLine = true)
                    OutlinedTextField(email, { email = it }, label = { Text("E-mail") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(enabled = !carregando && nome.isNotBlank() && email.contains("@"), onClick = {
                    val id = vendedor.id ?: return@Button
                    scope.launch {
                        carregando = true
                        apiService.atualizarVendedor(id, nome.trim(), email.trim(), vendedor.perfil, vendedor.ativo)
                            .onSuccess { editando = null; mensagem = "Vendedor atualizado."; carregar() }
                            .onFailure { mensagem = it.message ?: "Não foi possível atualizar o vendedor." }
                        carregando = false
                    }
                }) { Text("Salvar") }
            },
            dismissButton = { TextButton(onClick = { editando = null }) { Text("Cancelar") } }
        )
    }
}
