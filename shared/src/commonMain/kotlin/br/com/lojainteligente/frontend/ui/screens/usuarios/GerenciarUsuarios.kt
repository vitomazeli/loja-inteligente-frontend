package br.com.lojainteligente.frontend.ui.screens.usuarios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.repository.ApiService
import br.com.lojainteligente.frontend.data.repository.CadastroUsuarioResponseApi
import br.com.lojainteligente.frontend.ui.components.KoraActionIcon
import br.com.lojainteligente.frontend.ui.components.KoraConfirmationDialog
import br.com.lojainteligente.frontend.ui.components.KoraDataCard
import br.com.lojainteligente.frontend.ui.components.KoraEmptyState
import br.com.lojainteligente.frontend.ui.components.KoraFormField
import br.com.lojainteligente.frontend.ui.components.KoraIconAction
import br.com.lojainteligente.frontend.ui.components.KoraInitialsAvatar
import br.com.lojainteligente.frontend.ui.components.KoraLoadingState
import br.com.lojainteligente.frontend.ui.components.KoraSearchField
import br.com.lojainteligente.frontend.ui.components.KoraSectionHeader
import br.com.lojainteligente.frontend.ui.components.KoraStatusBadge
import kotlinx.coroutines.launch

@Composable
fun GerenciarUsuarios(apiService: ApiService) {
    var vendedores by remember { mutableStateOf<List<CadastroUsuarioResponseApi>>(emptyList()) }
    var carregando by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }
    var busca by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf<CadastroUsuarioResponseApi?>(null) }
    var vendedorParaAlterar by remember { mutableStateOf<CadastroUsuarioResponseApi?>(null) }
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val filtrados = vendedores.filter { usuario ->
        val termo = busca.trim()
        termo.isBlank() || listOfNotNull(usuario.nome, usuario.email, usuario.perfil, usuario.id?.toString())
            .any { it.contains(termo, ignoreCase = true) }
    }

    suspend fun carregar(mensagemSucesso: String? = null) {
        carregando = true
        apiService.listarUsuarios()
            .onSuccess { todos ->
                vendedores = todos.filter { it.perfil.equals("VENDEDOR", true) }
                mensagem = mensagemSucesso.orEmpty()
            }
            .onFailure { mensagem = it.message ?: "Não foi possível carregar os vendedores." }
        carregando = false
    }

    LaunchedEffect(apiService) { carregar() }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        KoraSectionHeader(
            title = "Equipe de vendas",
            count = vendedores.size,
            addLabel = "",
            onAdd = {},
            onRefresh = { scope.launch { carregar() } },
            refreshing = carregando
        )
        KoraSearchField(busca, { busca = it }, "Buscar por nome, e-mail ou ID")
        if (mensagem.isNotBlank()) {
            val sucesso = mensagem.contains("ativado", true) || mensagem.contains("desativado", true) || mensagem.contains("atualizado", true)
            Text(mensagem, color = if (sucesso) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        }
        when {
            carregando && vendedores.isEmpty() -> KoraLoadingState("Carregando usuários...")
            filtrados.isEmpty() && vendedores.isEmpty() && mensagem.isBlank() ->
                KoraEmptyState("Nenhum vendedor cadastrado", "As contas com perfil vendedor aparecerão aqui.")
            filtrados.isEmpty() -> KoraEmptyState("Nenhum usuário encontrado", "Tente outro nome, e-mail ou ID.")
            else -> LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtrados, key = { it.id ?: it.email }) { vendedor ->
                    KoraDataCard(contentPadding = 14.dp, contentSpacing = 7.dp) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            KoraInitialsAvatar(vendedor.nome)
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(vendedor.nome, style = MaterialTheme.typography.titleMedium)
                                Text(vendedor.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            KoraStatusBadge(vendedor.ativo, "Ativo", "Desativado")
                        }
                        Text("Perfil: ${vendedor.perfil} · ID ${vendedor.id ?: "—"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                            KoraIconAction(KoraActionIcon.Edit, onClick = {
                                editando = vendedor
                                nome = vendedor.nome
                                email = vendedor.email
                                mensagem = ""
                            }, enabled = !carregando)
                            TextButton(
                                onClick = { vendedorParaAlterar = vendedor },
                                enabled = !carregando,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = if (vendedor.ativo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Text(if (vendedor.ativo) "Desativar" else "Ativar")
                            }
                        }
                    }
                }
            }
        }
    }

    vendedorParaAlterar?.let { vendedor ->
        val ativoNovo = !vendedor.ativo
        KoraConfirmationDialog(
            title = if (ativoNovo) "Ativar vendedor?" else "Desativar vendedor?",
            message = if (ativoNovo) "A conta de ${vendedor.nome} poderá voltar a acessar o sistema." else "A conta de ${vendedor.nome} perderá o acesso ao sistema.",
            confirmLabel = if (ativoNovo) "Ativar" else "Desativar",
            onConfirm = {
                vendedorParaAlterar = null
                scope.launch {
                    apiService.atualizarStatusVendedor(vendedor, ativo = ativoNovo)
                        .onSuccess {
                            carregar(if (ativoNovo) "Vendedor ativado." else "Vendedor desativado.")
                        }
                        .onFailure { mensagem = it.message ?: "Não foi possível atualizar o status do vendedor." }
                }
            },
            onDismiss = { vendedorParaAlterar = null }
        )
    }

    editando?.let { vendedor ->
        AlertDialog(
            onDismissRequest = { if (!carregando) editando = null },
            title = { Text("Editar vendedor") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    KoraFormField(nome, { nome = it }, "Nome")
                    KoraFormField(email, { email = it }, "E-mail")
                    if (mensagem.isNotBlank()) Text(mensagem, color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                TextButton(enabled = !carregando && nome.isNotBlank() && email.contains("@"), onClick = {
                    val id = vendedor.id ?: return@TextButton
                    scope.launch {
                        apiService.atualizarVendedor(id, nome.trim(), email.trim(), vendedor.perfil, vendedor.ativo)
                            .onSuccess { editando = null; carregar("Vendedor atualizado.") }
                            .onFailure { mensagem = it.message ?: "Não foi possível atualizar o vendedor." }
                    }
                }) { Text(if (carregando) "Salvando..." else "Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { editando = null }, enabled = !carregando) { Text("Cancelar") }
            }
        )
    }
}
