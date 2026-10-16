
package br.com.lojainteligente.frontend.model

import kotlinx.serialization.Serializable

@Serializable
data class Categoria(
    val id: Long? = null,
    val nome: String = "",
    val descricao: String? = null,
    val ativo: Boolean = true,
    val dataCadastro: String = "",
    val ordemExibicao: Int = 0
)
