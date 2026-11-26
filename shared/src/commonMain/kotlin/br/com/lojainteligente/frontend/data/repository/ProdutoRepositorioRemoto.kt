package br.com.lojainteligente.frontend.data.repository

import br.com.lojainteligente.frontend.model.ProdutoRequestApi
import br.com.lojainteligente.frontend.model.ProdutoResponseApi

class ProdutoRepositorioRemoto(
    private val apiService: ApiService
) {

    suspend fun listar(): Result<List<ProdutoResponseApi>> {
        return apiService.listarProdutos()
    }

    suspend fun cadastrar(
        produto: ProdutoRequestApi
    ): Result<ProdutoResponseApi> {
        return apiService.cadastrarProduto(produto)
    }

    suspend fun atualizar(
        id: Long,
        produto: ProdutoRequestApi
    ): Result<ProdutoResponseApi> {
        return apiService.atualizarProduto(id, produto)
    }

    suspend fun excluir(id: Long): Result<Unit> {
        return apiService.excluirProduto(id)
    }
}