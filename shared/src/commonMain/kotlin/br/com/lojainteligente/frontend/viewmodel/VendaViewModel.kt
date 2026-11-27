package br.com.lojainteligente.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lojainteligente.frontend.data.repository.VendaRepositorioRemoto
import br.com.lojainteligente.frontend.model.VendaRequestApi
import br.com.lojainteligente.frontend.model.VendaResponseApi
import br.com.lojainteligente.frontend.model.ItemVendaRequestApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VendaUiState(
    val vendas: List<VendaResponseApi> = emptyList(),
    val clienteId: Long? = null,
    val itens: List<ItemVendaRequestApi> = emptyList(),
    val status: String = "PENDENTE",
    val observacao: String = "",
    val carregando: Boolean = false,
    val mensagem: String = ""
)

class VendaViewModel(private val repositorio: VendaRepositorioRemoto) : ViewModel() {
    private val _uiState = MutableStateFlow(VendaUiState())
    val uiState = _uiState.asStateFlow()

    fun selecionarCliente(id: Long?) = _uiState.update { it.copy(clienteId = id) }
    fun atualizarItens(itens: List<ItemVendaRequestApi>) = _uiState.update { it.copy(itens = itens) }
    fun atualizarStatus(valor: String) = _uiState.update { it.copy(status = valor) }
    fun atualizarObservacao(valor: String) = _uiState.update { it.copy(observacao = valor) }
    fun limparFormulario() = _uiState.update {
        it.copy(clienteId = null, itens = emptyList(), status = "PENDENTE", observacao = "", mensagem = "")
    }

    fun carregar() = viewModelScope.launch {
        _uiState.update { it.copy(carregando = true, mensagem = "") }
        carregarInterno()
        _uiState.update { it.copy(carregando = false) }
    }

    private suspend fun carregarInterno() {
        repositorio.listar().fold(
            { lista -> _uiState.update { it.copy(vendas = lista, carregando = false) } },
            { erro -> _uiState.update { it.copy(carregando = false, mensagem = erro.message ?: "Erro ao carregar vendas.") } }
        )
    }

    fun salvar(id: Long?, request: VendaRequestApi) = viewModelScope.launch {
        _uiState.update { it.copy(carregando = true, mensagem = "") }
        val resultado = id?.let { repositorio.atualizar(it, request) } ?: repositorio.cadastrar(request)
        resultado.fold(
            { _ -> carregarInterno(); _uiState.update { it.copy(carregando = false, mensagem = if (id == null) "Venda cadastrada com sucesso." else "Venda atualizada com sucesso.") } },
            { erro -> _uiState.update { it.copy(carregando = false, mensagem = erro.message ?: "Erro ao salvar venda.") } }
        )
    }

    fun excluir(id: Long) = viewModelScope.launch {
        _uiState.update { it.copy(carregando = true, mensagem = "") }
        repositorio.excluir(id).fold(
            { carregarInterno(); _uiState.update { it.copy(carregando = false, mensagem = "Venda excluída.") } },
            { erro -> _uiState.update { it.copy(carregando = false, mensagem = erro.message ?: "Erro ao excluir venda.") } }
        )
    }

    fun limparMensagem() = _uiState.update { it.copy(mensagem = "") }
}
