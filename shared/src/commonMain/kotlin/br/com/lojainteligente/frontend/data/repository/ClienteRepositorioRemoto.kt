package br.com.lojainteligente.frontend.data.repository

import br.com.lojainteligente.frontend.model.ClienteRequestApi
import br.com.lojainteligente.frontend.model.ClienteResponseApi

class ClienteRepositorioRemoto(private val api: ApiService) {
    suspend fun listar() = api.listarClientes()
    suspend fun cadastrar(cliente: ClienteRequestApi) = api.cadastrarCliente(cliente)
    suspend fun atualizar(id: Long, cliente: ClienteRequestApi) = api.atualizarCliente(id, cliente)
    suspend fun excluir(id: Long) = api.excluirCliente(id)
}
