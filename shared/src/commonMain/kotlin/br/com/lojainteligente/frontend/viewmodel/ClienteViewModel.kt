package br.com.lojainteligente.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lojainteligente.frontend.data.repository.ClienteRepositorioRemoto
import br.com.lojainteligente.frontend.model.ClienteRequestApi
import br.com.lojainteligente.frontend.model.ClienteResponseApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClienteUiState(
    val clientes: List<ClienteResponseApi> = emptyList(),
    val id: Long? = null,
    val nome: String = "",
    val email: String = "",
    val telefone: String = "",
    val ativo: Boolean = true,
    val carregando: Boolean = false,
    val mensagem: String = ""
)

class ClienteViewModel(private val repositorio: ClienteRepositorioRemoto) : ViewModel() {
    private val _uiState = MutableStateFlow(ClienteUiState())
    val uiState = _uiState.asStateFlow()

    fun atualizarNome(valor: String) = _uiState.update { it.copy(nome = valor) }
    fun atualizarEmail(valor: String) = _uiState.update { it.copy(email = valor) }
    fun atualizarTelefone(valor: String) = _uiState.update { it.copy(telefone = valor) }
    fun atualizarAtivo(valor: Boolean) = _uiState.update { it.copy(ativo = valor) }

    fun carregar() = viewModelScope.launch {
        _uiState.update { it.copy(carregando = true, mensagem = "") }
        carregarInterno()
        _uiState.update { it.copy(carregando = false) }
    }

    private suspend fun carregarInterno() {
        repositorio.listar().fold(
            { lista -> _uiState.update { it.copy(clientes = lista, carregando = false) } },
            { erro -> _uiState.update { it.copy(carregando = false, mensagem = erro.message ?: "Erro ao carregar clientes.") } }
        )
    }

    fun selecionar(cliente: ClienteResponseApi) = _uiState.update {
        it.copy(id = cliente.id, nome = cliente.nome, email = cliente.email, telefone = cliente.telefone.orEmpty(), ativo = cliente.ativo, mensagem = "")
    }

    fun salvar(request: ClienteRequestApi) = viewModelScope.launch {
        val id = _uiState.value.id
        _uiState.update { it.copy(carregando = true, mensagem = "") }
        val resultado = id?.let { repositorio.atualizar(it, request) } ?: repositorio.cadastrar(request)
        resultado.fold(
            { _ -> carregarInterno(); _uiState.update { it.copy(carregando = false, mensagem = if (id == null) "Cliente cadastrado com sucesso." else "Cliente atualizado com sucesso.") } },
            { erro -> _uiState.update { it.copy(carregando = false, mensagem = erro.message ?: "Erro ao salvar cliente.") } }
        )
    }

    fun excluir(id: Long) = viewModelScope.launch {
        _uiState.update { it.copy(carregando = true, mensagem = "") }
        repositorio.excluir(id).fold(
            { carregarInterno(); _uiState.update { it.copy(carregando = false, mensagem = "Cliente excluído.") } },
            { erro -> _uiState.update { it.copy(carregando = false, mensagem = erro.message ?: "Erro ao excluir cliente.") } }
        )
    }

    fun limpar() = _uiState.update { it.copy(id = null, nome = "", email = "", telefone = "", ativo = true, mensagem = "") }
    fun limparMensagem() = _uiState.update { it.copy(mensagem = "") }
}
