
package br.com.lojainteligente.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lojainteligente.frontend.data.repository.CategoriaRepositorioRemoto
import br.com.lojainteligente.frontend.data.repository.CategoriaRequestApi
import br.com.lojainteligente.frontend.data.repository.CategoriaResponseApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoriaUiState(
    val id: Long? = null,
    val nome: String = "",
    val descricao: String = "",
    val ativo: Boolean = true,
    val ordemExibicao: String = "0",
    val categorias: List<CategoriaResponseApi> = emptyList(),
    val carregando: Boolean = false,
    val mensagem: String = ""
)

class CategoriaViewModel(
    private val repositorio: CategoriaRepositorioRemoto
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState: StateFlow<CategoriaUiState> = _uiState.asStateFlow()

    fun atualizarNome(valor: String) {
        _uiState.update { it.copy(nome = valor) }
    }

    fun atualizarDescricao(valor: String) {
        _uiState.update { it.copy(descricao = valor) }
    }

    fun atualizarAtivo(valor: Boolean) {
        _uiState.update { it.copy(ativo = valor) }
    }

    fun atualizarOrdemExibicao(valor: String) {
        _uiState.update { it.copy(ordemExibicao = valor) }
    }

    // Carregamento público, chamado pela tela
    fun carregarCategorias() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(carregando = true, mensagem = "")
            }

            carregarCategoriasInterno()

            _uiState.update {
                it.copy(carregando = false)
            }
        }
    }

    // Carregamento interno que aguarda a resposta da API
    private suspend fun carregarCategoriasInterno() {
        repositorio.listar().fold(
            onSuccess = { lista ->
                _uiState.update {
                    it.copy(
                        categorias = lista,
                        mensagem = ""
                    )
                }
            },
            onFailure = { erro ->
                _uiState.update {
                    it.copy(
                        mensagem = erro.message
                            ?: "Erro ao carregar categorias."
                    )
                }
            }
        )
    }

    fun selecionarCategoria(categoria: CategoriaResponseApi) {
        _uiState.update {
            it.copy(
                id = categoria.id,
                nome = categoria.nome,
                descricao = categoria.descricao ?: "",
                ativo = categoria.ativo,
                ordemExibicao = categoria.ordemExibicao.toString(),
                mensagem = ""
            )
        }
    }

    fun salvarCategoria() {
        val estado = _uiState.value
        val ordem = estado.ordemExibicao.toIntOrNull()

        if (estado.nome.isBlank() || ordem == null || ordem < 0) {
            _uiState.update {
                it.copy(
                    mensagem = "Informe um nome e uma ordem de exibição válida."
                )
            }
            return
        }

        if (estado.nome.trim().length > 100) {
            _uiState.update {
                it.copy(
                    mensagem = "O nome deve ter no máximo 100 caracteres."
                )
            }
            return
        }

        val categoria = CategoriaRequestApi(
            nome = estado.nome.trim(),
            descricao = estado.descricao.trim().ifBlank { null },
            ativo = estado.ativo,
            ordemExibicao = ordem
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(carregando = true, mensagem = "")
            }

            val resultado = if (estado.id == null) {
                repositorio.cadastrar(categoria)
            } else {
                repositorio.atualizar(estado.id, categoria)
            }

            resultado.fold(
                onSuccess = {
                    limparFormulario()
                    carregarCategoriasInterno()

                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagem = "Categoria salva com sucesso."
                        )
                    }
                },
                onFailure = { erro ->
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagem = erro.message
                                ?: "Erro ao salvar categoria."
                        )
                    }
                }
            )
        }
    }

    fun excluirCategoria(id: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(carregando = true, mensagem = "")
            }

            repositorio.excluir(id).fold(
                onSuccess = {
                    carregarCategoriasInterno()

                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagem = "Categoria excluída com sucesso."
                        )
                    }
                },
                onFailure = { erro ->
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagem = erro.message
                                ?: "Erro ao excluir categoria."
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
                ativo = true,
                ordemExibicao = "0",
                mensagem = ""
            )
        }
    }
}
