package br.com.lojainteligente.frontend.data.repository

import br.com.lojainteligente.frontend.model.VendaRequestApi

class VendaRepositorioRemoto(private val api: ApiService) {
    suspend fun listar() = api.listarVendas()
    suspend fun cadastrar(venda: VendaRequestApi) = api.cadastrarVenda(venda)
    suspend fun atualizar(id: Long, venda: VendaRequestApi) = api.atualizarVenda(id, venda)
    suspend fun excluir(id: Long) = api.excluirVenda(id)
}
