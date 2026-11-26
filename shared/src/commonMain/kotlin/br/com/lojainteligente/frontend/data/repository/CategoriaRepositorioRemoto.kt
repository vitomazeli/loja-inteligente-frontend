
package br.com.lojainteligente.frontend.data.repository

class CategoriaRepositorioRemoto(
    private val apiService: ApiService
) {

    suspend fun listar(): Result<List<CategoriaResponseApi>> {
        return apiService.listarCategorias()
    }

    suspend fun cadastrar(
        categoria: CategoriaRequestApi
    ): Result<CategoriaResponseApi> {
        return apiService.cadastrarCategoria(categoria)
    }

    suspend fun atualizar(
        id: Long,
        categoria: CategoriaRequestApi
    ): Result<CategoriaResponseApi> {
        return apiService.atualizarCategoria(id, categoria)
    }

    suspend fun excluir(id: Long): Result<Unit> {
        return apiService.excluirCategoria(id)
    }
}
