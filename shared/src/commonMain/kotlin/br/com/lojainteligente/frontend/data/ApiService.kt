
package br.com.lojainteligente.frontend.data

import br.com.lojainteligente.frontend.model.ClienteRequestApi
import br.com.lojainteligente.frontend.model.ClienteResponseApi
import br.com.lojainteligente.frontend.model.ProdutoRequestApi
import br.com.lojainteligente.frontend.model.ProdutoResponseApi
import br.com.lojainteligente.frontend.model.VendaRequestApi
import br.com.lojainteligente.frontend.model.VendaResponseApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


private const val BASE_URL = "http://localhost:8082"

@Serializable
data class LoginRequestApi(
    val email: String,
    val senha: String
)

@Serializable
data class LoginResponseApi(
    val token: String
)


@Serializable
data class CadastroUsuarioRequestApi(
    val nome: String,
    val email: String,
    val senha: String
)

@Serializable
data class CadastroUsuarioResponseApi(
    val id: Long? = null,
    val nome: String,
    val email: String,
    val perfil: String,
    val ativo: Boolean
)

@Serializable
data class CategoriaRequestApi(
    val nome: String,
    val descricao: String? = null,
    val ativo: Boolean = true,
    val ordemExibicao: Int = 0
)

@Serializable
data class CategoriaResponseApi(
    val id: Long? = null,
    val nome: String,
    val descricao: String? = null,
    val ativo: Boolean = true,
    val ordemExibicao: Int = 0,
    val dataCadastro: String = ""
)

class ApiService(engine: HttpClientEngine) {

    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                }
            )
        }
    }

    var token: String? = null
        private set

    // LOGIN

    suspend fun login(
        email: String,
        senha: String
    ): Result<Unit> = runCatching {
        val resposta = client.post("$BASE_URL/auth/login") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(LoginRequestApi(email, senha))
        }

        if (resposta.status.value !in 200..299) {
            error("Login inválido. Confira seu e-mail e sua senha.")
        }

        val dados = resposta.body<LoginResponseApi>()
        token = dados.token
    }


    suspend fun cadastrarUsuario(
        usuario: CadastroUsuarioRequestApi
    ): Result<String> = runCatching {
        val resposta = client.post("$BASE_URL/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(usuario)
        }

        val mensagem = resposta.bodyAsText()

        if (resposta.status.value !in 200..299) {
            error(
                mensagem.ifBlank {
                    "Erro HTTP ${resposta.status.value} ao cadastrar usuário."
                }
            )
        }

        mensagem
    }

    suspend fun listarUsuarios():
            Result<List<CadastroUsuarioResponseApi>> =
        executarGet("/api/usuarios")

    // CATEGORIAS

    suspend fun cadastrarCategoria(
        categoria: CategoriaRequestApi
    ): Result<CategoriaResponseApi> =
        executarPost("/api/categorias", categoria)

    suspend fun listarCategorias():
            Result<List<CategoriaResponseApi>> =
        executarGet("/api/categorias")

    // PRODUTOS

    suspend fun cadastrarProduto(
        produto: ProdutoRequestApi
    ): Result<ProdutoResponseApi> =
        executarPost("/api/produtos", produto)

    suspend fun listarProdutos():
            Result<List<ProdutoResponseApi>> =
        executarGet("/api/produtos")

    // CLIENTES

    suspend fun cadastrarCliente(
        cliente: ClienteRequestApi
    ): Result<ClienteResponseApi> =
        executarPost("/api/clientes", cliente)

    suspend fun listarClientes():
            Result<List<ClienteResponseApi>> =
        executarGet("/api/clientes")

    // VENDAS

    suspend fun cadastrarVenda(
        venda: VendaRequestApi
    ): Result<VendaResponseApi> =
        executarPost("/api/vendas", venda)

    suspend fun listarVendas():
            Result<List<VendaResponseApi>> =
        executarGet("/api/vendas")

    // MÉTODOS HTTP REUTILIZÁVEIS

    private suspend inline fun <reified T> executarGet(
        caminho: String
    ): Result<T> = runCatching {
        val jwt = token
            ?: error("Faça login antes de continuar.")

        val resposta = client.get("$BASE_URL$caminho") {
            accept(ContentType.Application.Json)
            bearerAuth(jwt)
        }

        if (resposta.status.value !in 200..299) {
            val detalhe = resposta.bodyAsText()
            error(
                detalhe.ifBlank {
                    "Erro HTTP ${resposta.status.value} ao consultar $caminho."
                }
            )
        }

        resposta.body<T>()
    }

    private suspend inline fun <reified R, reified T> executarPost(
        caminho: String,
        corpo: R
    ): Result<T> = runCatching {
        val jwt = token
            ?: error("Faça login antes de continuar.")

        val resposta = client.post("$BASE_URL$caminho") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            bearerAuth(jwt)
            setBody(corpo)
        }

        if (resposta.status.value !in 200..299) {
            val detalhe = resposta.bodyAsText()
            error(
                detalhe.ifBlank {
                    "Erro HTTP ${resposta.status.value} ao enviar dados para $caminho."
                }
            )
        }

        resposta.body<T>()
    }

    // SESSÃO E ENCERRAMENTO

    fun logout() {
        token = null
    }

    fun fechar() {
        client.close()
    }
}
