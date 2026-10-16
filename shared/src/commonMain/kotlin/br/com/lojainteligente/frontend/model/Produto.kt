
package br.com.lojainteligente.frontend.model

import kotlinx.serialization.Serializable

@Serializable
data class ProdutoRequestApi(
    val nome: String,
    val descricao: String? = null,
    val codigoBarras: String,
    val preco: Double,
    val categoriaId: Long,
    val ativo: Boolean = true
)

@Serializable
data class ProdutoResponseApi(
    val id: Long? = null,
    val nome: String,
    val descricao: String? = null,
    val codigoBarras: String,
    val preco: Double,
    val categoriaId: Long? = null,
    val categoriaNome: String? = null,
    val ativo: Boolean = true,
    val dataCadastro: String
)
