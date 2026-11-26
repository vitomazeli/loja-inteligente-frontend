
package br.com.lojainteligente.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lojainteligente.frontend.data.repository.ProdutoRepositorioRemoto
import br.com.lojainteligente.frontend.model.ProdutoRequestApi
import br.com.lojainteligente.frontend.model.ProdutoResponseApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProdutoUiState(
    val id: Long? = null,
    val nome: String = "",
    val descricao: String = "",
    val codigoBarras: String = "",
    val preco: String = "",
    val categoriaId: String = "",
    val ativo: Boolean = true,
    val produtos: List<ProdutoResponseApi> = emptyList(),
    val carregando: Boolean = false,
    val mensagem: String = ""
)

class ProdutoViewModel(
    private val repositorio: ProdutoRepositorioRemoto
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProdutoUiState())
    val uiState: StateFlow<ProdutoUiState> = _uiState.asStateFlow()

    fun atualizarNome(valor: String) {
        _uiState.update { it.copy(nome = valor) }
    }

    fun atualizarDescricao(valor: String) {
        _uiState.update { it.copy(descricao = valor) }
    }

    fun atualizarCodigoBarras(valor: String) {
        _uiState.update { it.copy(codigoBarras = valor) }
    }

    fun atualizarPreco(valor: String) {
        _uiState.update { it.copy(preco = valor) }
    }

    fun atualizarCategoriaId(valor: String) {
        _uiState.update { it.copy(categoriaId = valor) }
    }

    fun atualizarAtivo(valor: Boolean) {
        _uiState.update { it.copy(ativo = valor) }
    }

    fun carregarProdutos() {
        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true) }
            carregarProdutosInterno()
            _uiState.update { it.copy(carregando = false) }
        }
    }

    private suspend fun carregarProdutosInterno() {
        repositorio.listar().fold(
            onSuccess = { lista -> _uiState.update { it.copy(produtos = lista, mensagem = "") } },
            onFailure = { erro -> _uiState.update { it.copy(mensagem = erro.message ?: "Erro ao carregar produtos.") } }
        )
    }

    fun selecionarProduto(produto: ProdutoResponseApi) {
        _uiState.update {
            it.copy(
                id = produto.id,
                nome = produto.nome,
                descricao = produto.descricao ?: "",
                codigoBarras = produto.codigoBarras,
                preco = produto.preco.toString(),
                categoriaId = produto.categoriaId?.toString() ?: "",
                ativo = produto.ativo,
                mensagem = ""
            )
        }
    }

    fun salvarProduto() {
        val estado = _uiState.value
        val estavaEditando = estado.id != null
        val precoConvertido = estado.preco
            .trim()
            .replace(",", ".")
            .toDoubleOrNull()
        val categoriaConvertida = estado.categoriaId.toLongOrNull()

        if (estado.nome.isBlank() ||
            estado.codigoBarras.isBlank() ||
            precoConvertido == null ||
            categoriaConvertida == null
        ) {
            _uiState.update {
                it.copy(mensagem = "Preencha os campos obrigatórios corretamente.")
            }
            return
        }

        val produto = ProdutoRequestApi(
            nome = estado.nome,
            descricao = estado.descricao.ifBlank { null },
            codigoBarras = estado.codigoBarras,
            preco = precoConvertido,
            categoriaId = categoriaConvertida,
            ativo = estado.ativo
        )

        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, mensagem = "") }

            val resultado = if (estado.id == null) {
                repositorio.cadastrar(produto)
            } else {
                repositorio.atualizar(estado.id, produto)
            }

            resultado.fold(
                onSuccess = {
                    limparFormulario()
                    carregarProdutosInterno()
                    _uiState.update {
                        it.copy(mensagem = if (estavaEditando) "Produto atualizado com sucesso." else "Produto cadastrado com sucesso.")
                    }
                },
                onFailure = { erro ->
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagem = erro.message ?: "Erro ao salvar produto."
                        )
                    }
                }
            )
        }
    }

    fun excluirProduto(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, mensagem = "") }

            repositorio.excluir(id).fold(
                onSuccess = {
                    carregarProdutosInterno()
                    _uiState.update {
                        it.copy(mensagem = "Produto excluído com sucesso.")
                    }
                },
                onFailure = { erro ->
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagem = erro.message ?: "Erro ao excluir produto."
                        )
                    }
                }
            )
        }
    }

    fun limparFormulario() {
        _uiState.update {
            it.copy(
                id = null,
                nome = "",
                descricao = "",
                codigoBarras = "",
                preco = "",
                categoriaId = "",
                ativo = true
            )
        }
    }

    fun limparMensagem() {
        _uiState.update { it.copy(mensagem = "") }
    }
}
