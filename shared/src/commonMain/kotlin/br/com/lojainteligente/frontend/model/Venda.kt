
package br.com.lojainteligente.frontend.model

import kotlinx.serialization.Serializable

@Serializable
data class ItemVendaRequestApi(
    val produtoId: Long,
    val quantidade: Int,
    val desconto: Double = 0.0
)

@Serializable
data class VendaRequestApi(
    val clienteId: Long,
    val itens: List<ItemVendaRequestApi>,
    val observacao: String? = null
)

@Serializable
data class ItemVendaResponseApi(
    val id: Long? = null,
    val produtoId: Long? = null,
    val produtoNome: String? = null,
    val quantidade: Int,
    val precoUnitario: Double,
    val subtotal: Double,
    val desconto: Double
)

@Serializable
data class VendaResponseApi(
    val id: Long? = null,
    val clienteId: Long? = null,
    val clienteNome: String? = null,
    val dataVenda: String,
    val total: Double,
    val status: String,
    val observacao: String? = null,
    val itens: List<ItemVendaResponseApi> = emptyList()
)
