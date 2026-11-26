
package br.com.lojainteligente.frontend


import br.com.lojainteligente.frontend.data.repository.ApiService
import br.com.lojainteligente.frontend.data.createHttpClientEngine
import br.com.lojainteligente.frontend.ui.screens.categorias.CategoriaForm
import br.com.lojainteligente.frontend.ui.screens.categorias.CategoriaLista
import br.com.lojainteligente.frontend.ui.screens.clientes.ClienteForm
import br.com.lojainteligente.frontend.ui.screens.clientes.ClienteLista
import br.com.lojainteligente.frontend.ui.screens.login.LoginForm
import br.com.lojainteligente.frontend.ui.screens.produtos.ProdutoForm
import br.com.lojainteligente.frontend.ui.screens.produtos.ProdutoLista
import br.com.lojainteligente.frontend.ui.screens.vendas.VendaForm
import br.com.lojainteligente.frontend.ui.screens.vendas.VendaLista
import br.com.lojainteligente.frontend.data.repository.ProdutoRepositorioRemoto
import br.com.lojainteligente.frontend.viewmodel.ProdutoViewModel
import br.com.lojainteligente.frontend.data.repository.CategoriaRepositorioRemoto
import br.com.lojainteligente.frontend.viewmodel.CategoriaViewModel


import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.entryProvider
import androidx.savedstate.serialization.SavedStateConfiguration

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

import io.ktor.client.engine.cio.CIO


private val AzulEscuro = Color(0xFF172554)
private val Azul = Color(0xFF2563EB)
private val Fundo = Color(0xFFF4F7FB)
private val TextoEscuro = Color(0xFF1E293B)
private val TextoSecundario = Color(0xFF64748B)

private val AzulClaro = Color(0xFFDBEAFE)
private val AzulBorda = Color(0xFFBFDBFE)

private val CoresLojaInteligente = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulEscuro,

    secondary = AzulEscuro,
    onSecondary = Color.White,

    background = Fundo,
    onBackground = TextoEscuro,

    surface = Color.White,
    onSurface = TextoEscuro,

    surfaceVariant = AzulClaro,
    onSurfaceVariant = TextoSecundario,

    outline = AzulBorda,
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
private fun LojaInteligenteTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CoresLojaInteligente,
        content = content
    )
}



@Composable
fun App() {

    var versaoProdutos by remember { mutableStateOf(0) }
    var versaoClientes by remember { mutableStateOf(0) }
    var versaoVendas by remember { mutableStateOf(0) }
    var versaoUsuarios by remember { mutableStateOf(0) }
    var versaoCategorias by remember { mutableStateOf(0) }
    var versaoDashboard by remember { mutableStateOf(0) }

    val backStack = rememberNavBackStack(

        configuration = SavedStateConfiguration {

            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(DashboardRoute::class)
                    subclass(Produtos::class)
                    subclass(Categorias::class)
                    subclass(Clientes::class)
                    subclass(Vendas::class)
                    subclass(ListaCategorias::class)
                    subclass(FormularioCategoria::class)
                    subclass(ListaProdutos::class)
                    subclass(FormularioProduto::class)
                    subclass(ListaClientes::class)
                    subclass(FormularioCliente::class)
                    subclass(ListaVendas::class)
                    subclass(FormularioVenda::class)
                }
            }
        },
        DashboardRoute
    )
    val apiService = remember {
        ApiService(CIO.create())
    }

    val categoriaViewModel = remember(apiService) {
        CategoriaViewModel(
            CategoriaRepositorioRemoto(apiService)
        )
    }

    val rotaAtual = backStack.lastOrNull() as? Rota ?: DashboardRoute

    LojaInteligenteTheme {
        val apiService = remember {
            ApiService(createHttpClientEngine())
        }

        var autenticado by remember {
            mutableStateOf(false)
        }





        if (!autenticado) {
            LoginForm(
                aoEntrar = { email, senha ->
                    apiService.login(email, senha)
                },
                aoLoginSucesso = {
                    println("Token armazenado: ${apiService.possuiToken()}")
                    autenticado = true
                }
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Fundo)
            ) {
                MenuLateral(
                    rotaAtual = rotaAtual,
                    aoSelecionar = { rota ->
                        if (backStack.lastOrNull() != rota) {
                            backStack.add(rota)
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(24.dp)
                ) {
                    val tituloTela = when (rotaAtual) {
                        DashboardRoute -> "Dashboard"
                        Produtos -> "Produtos"
                        Categorias -> "Categorias"
                        Clientes -> "Clientes"
                        Vendas -> "Vendas"
                        else -> "Módulo"
                    }

                    Text(
                        text = tituloTela,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoEscuro
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Bem-vindo ao sistema de gestão da sua loja.",
                        color = TextoSecundario,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(24.dp))



                    NavDisplay(
                        backStack = backStack,
                        onBack = {
                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        },
                        entryProvider = entryProvider {
                            entry<DashboardRoute> {
                                Dashboard(
                                    apiService = apiService,
                                    atualizarVersao = versaoDashboard,
                                    aoSelecionar = { rota ->
                                        if (backStack.lastOrNull() != rota) {
                                            backStack.add(rota)
                                        }
                                    }
                                )
                            }


                            entry<Categorias> {
                                CategoriaLista(
                                    viewModel = categoriaViewModel,
                                    atualizarVersao = versaoCategorias,
                                    aoEditar = { categoria ->
                                        categoriaViewModel.selecionarCategoria(categoria)
                                        backStack.add(FormularioCategoria)
                                    },
                                    aoNovaCategoria = {
                                        categoriaViewModel.limparFormulario()
                                        backStack.add(FormularioCategoria)
                                    }
                                )
                            }


                            entry<Produtos> {
                                val produtoViewModel = remember(apiService) {
                                    ProdutoViewModel(
                                        ProdutoRepositorioRemoto(apiService)
                                    )
                                }

                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    ProdutoForm(
                                        apiService = apiService,
                                        viewModel = produtoViewModel,
                                        onProdutoCadastrado = {
                                            versaoProdutos++
                                            versaoDashboard++
                                        }
                                    )

                                    HorizontalDivider()

                                    ProdutoLista(
                                        viewModel = produtoViewModel,
                                        atualizarVersao = versaoProdutos,
                                        aoEditar = { produto ->
                                            produtoViewModel.selecionarProduto(produto)
                                        }
                                    )
                                }
                            }


                            entry<Clientes> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    ClienteForm(
                                        apiService = apiService,
                                        onClienteCadastrado = {
                                            versaoClientes++
                                            versaoDashboard++
                                        }
                                    )

                                    HorizontalDivider()

                                    ClienteLista(
                                        apiService = apiService,
                                        atualizarVersao = versaoClientes
                                    )
                                }
                            }

                            entry<Vendas> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    VendaForm(
                                        apiService = apiService,
                                        onVendaCadastrada = {
                                            versaoVendas++
                                            versaoDashboard++
                                        }
                                    )

                                    HorizontalDivider()

                                    VendaLista(
                                        apiService = apiService,
                                        atualizarVersao = versaoVendas
                                    )
                                }
                            }

                            entry<ListaCategorias> {
                                TelaEmConstrucao("Lista de Categorias")
                            }

                            entry<FormularioCategoria> {
                                CategoriaForm(
                                    viewModel = categoriaViewModel,
                                    onCategoriaCadastrada = {
                                        versaoCategorias++
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }

                            entry<ListaProdutos> {
                                TelaEmConstrucao("Lista de Produtos")
                            }

                            entry<FormularioProduto> {
                                TelaEmConstrucao("Formulário de Produto")
                            }

                            entry<ListaClientes> {
                                TelaEmConstrucao("Lista de Clientes")
                            }

                            entry<FormularioCliente> {
                                TelaEmConstrucao("Formulário de Cliente")
                            }

                            entry<ListaVendas> {
                                TelaEmConstrucao("Lista de Vendas")
                            }

                            entry<FormularioVenda> {
                                TelaEmConstrucao("Formulário de Venda")
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun MenuLateral(
    rotaAtual: Rota,
    aoSelecionar: (Rota) -> Unit
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(AzulEscuro)
            .padding(16.dp)
    ) {
        Text(
            text = "KORA",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "GESTÃO COMERCIAL",
            fontSize = 10.sp,
            color = Color(0xFFBFDBFE),
            letterSpacing = 1.sp
        )

        Spacer(Modifier.height(32.dp))

        val opcoes = listOf(
            "Dashboard" to DashboardRoute,
            "Produtos" to Produtos,
            "Categorias" to Categorias,

            "Clientes" to Clientes,
            "Vendas" to Vendas
        )

        opcoes.forEach { (nome, rota) ->

            val selecionado = rotaAtual == rota

            Surface(
                onClick = { aoSelecionar(rota) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (selecionado) Azul else Color.Transparent
            ) {
                Text(
                    text = nome,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 12.dp
                    ),
                    color = Color.White,
                    fontWeight = if (selecionado) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    },
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "Sistema de Controle",
            color = Color(0xFFBFDBFE),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun Dashboard(
    apiService: ApiService,
    atualizarVersao: Int,
    aoSelecionar: (Rota) -> Unit
) {
    var totalProdutos by remember { mutableStateOf("—") }
    var totalCategorias by remember { mutableStateOf("—") }
    var totalUsuarios by remember { mutableStateOf("—") }
    var totalClientes by remember { mutableStateOf("—") }
    var totalVendas by remember { mutableStateOf("—") }

    LaunchedEffect(apiService, atualizarVersao) {
        totalProdutos = apiService.listarProdutos()
            .getOrNull()?.size?.toString() ?: "—"

        totalCategorias = apiService.listarCategorias()
            .getOrNull()?.size?.toString() ?: "—"

        totalUsuarios = apiService.listarUsuarios()
            .getOrNull()?.size?.toString() ?: "—"

        totalClientes = apiService.listarClientes()
            .getOrNull()?.size?.toString() ?: "—"

        totalVendas = apiService.listarVendas()
            .getOrNull()?.size?.toString() ?: "—"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Resumo do sistema",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoEscuro
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Indicador(
                        titulo = "Produtos",
                        valor = totalProdutos,
                        descricao = "Total cadastrado",
                        modifier = Modifier.weight(1f)
                    )

                    Indicador(
                        titulo = "Categorias",
                        valor = totalCategorias,
                        descricao = "Categorias registradas",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Indicador(
                        titulo = "Usuários",
                        valor = totalUsuarios,
                        descricao = "Usuários cadastrados",
                        modifier = Modifier.weight(1f)
                    )

                    Indicador(
                        titulo = "Clientes",
                        valor = totalClientes,
                        descricao = "Total cadastrado",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Indicador(
                        titulo = "Vendas",
                        valor = totalVendas,
                        descricao = "Movimentações",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Acesso rápido",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoEscuro
                )

                listOf(
                    Pair("Produtos", Produtos),
                    Pair("Categorias", Categorias),
                    Pair("Clientes", Clientes),
                    Pair("Vendas", Vendas)
                ).forEach { (modulo, rota) ->
                    Surface(
                        onClick = { aoSelecionar(rota) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = modulo,
                                    color = TextoEscuro,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "Acessar módulo",
                                    color = TextoSecundario,
                                    fontSize = 12.sp
                                )
                            }

                            Text(
                                text = "Abrir →",
                                color = Azul,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Indicador(
    titulo: String,
    valor: String,
    descricao: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                color = TextoSecundario,
                fontSize = 13.sp
            )

            Text(
                text = valor,
                color = AzulEscuro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = descricao,
                color = TextoSecundario,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun TelaEmConstrucao(nome: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Módulo de $nome",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextoEscuro
            )

            Text(
                text = "Esta tela será implementada na próxima etapa.",
                color = TextoSecundario
            )
        }
    }
}
