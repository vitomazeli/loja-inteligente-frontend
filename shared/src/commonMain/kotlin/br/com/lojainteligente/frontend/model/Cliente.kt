
package br.com.lojainteligente.frontend.model

import kotlinx.serialization.Serializable

@Serializable
data class ClienteRequestApi(
    val nome: String,
    val email: String,
    val telefone: String? = null,
    val ativo: Boolean = true
)

@Serializable
data class ClienteResponseApi(
    val id: Long,
    val nome: String,
    val email: String,
    val telefone: String? = null,
    val ativo: Boolean = true,
    val dataCadastro: String = ""
)
