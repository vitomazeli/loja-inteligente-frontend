
package br.com.lojainteligente.frontend


import br.com.lojainteligente.frontend.data.repository.ApiService
import br.com.lojainteligente.frontend.data.repository.LeituraPrateleiraApi
import br.com.lojainteligente.frontend.data.createHttpClientEngine
import br.com.lojainteligente.frontend.ui.screens.categorias.CategoriaForm
import br.com.lojainteligente.frontend.ui.screens.categorias.CategoriaLista
import br.com.lojainteligente.frontend.ui.screens.clientes.ClienteForm
import br.com.lojainteligente.frontend.ui.screens.clientes.ClienteLista
import br.com.lojainteligente.frontend.ui.screens.login.LoginForm
import br.com.lojainteligente.frontend.ui.screens.cadastro.CadastroForm
import br.com.lojainteligente.frontend.ui.screens.produtos.ProdutoForm
import br.com.lojainteligente.frontend.ui.screens.produtos.ProdutoLista
import br.com.lojainteligente.frontend.ui.screens.vendas.VendaForm
import br.com.lojainteligente.frontend.ui.screens.vendas.VendaLista
import br.com.lojainteligente.frontend.data.repository.ProdutoRepositorioRemoto
import br.com.lojainteligente.frontend.viewmodel.ProdutoViewModel
import br.com.lojainteligente.frontend.data.repository.CategoriaRepositorioRemoto
import br.com.lojainteligente.frontend.viewmodel.CategoriaViewModel
import br.com.lojainteligente.frontend.data.repository.ClienteRepositorioRemoto
import br.com.lojainteligente.frontend.viewmodel.ClienteViewModel
import br.com.lojainteligente.frontend.data.repository.VendaRepositorioRemoto
import br.com.lojainteligente.frontend.viewmodel.VendaViewModel
import br.com.lojainteligente.frontend.ui.screens.usuarios.GerenciarUsuarios
import br.com.lojainteligente.frontend.model.ClienteResponseApi
import br.com.lojainteligente.frontend.model.VendaResponseApi


import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



private val AzulEscuro = Color(0xFF142447)
private val Azul = Color(0xFF315CF6)
private val Fundo = Color(0xFFF4F7FC)
private val TextoEscuro = Color(0xFF18243A)
private val TextoSecundario = Color(0xFF6B7891)
private val AzulClaro = Color(0xFFE8F0FF)
private val AzulBorda = Color(0xFFD8E3F5)
private val Verde = Color(0xFF159A79)

private val CoresLojaInteligente = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulEscuro,

    secondary = AzulEscuro,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE7EDF8),
    onSecondaryContainer = AzulEscuro,

    background = Fundo,
    onBackground = TextoEscuro,

    surface = Color.White,
    onSurface = TextoEscuro,

    surfaceVariant = AzulClaro,
    onSurfaceVariant = TextoSecundario,

    outline = AzulBorda,
    error = Color(0xFFDC2626),
    onError = Color.White,
    errorContainer = Color(0xFFFFE9E8),
    onErrorContainer = Color(0xFF8E1F20)
)

@Composable
private fun LojaInteligenteTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CoresLojaInteligente,
        typography = Typography(
            headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 36.sp),
            headlineSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp),
            titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
            titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
            bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 15.sp, lineHeight = 22.sp),
            bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp),
            bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, lineHeight = 17.sp),
            labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp)
        ),
        shapes = Shapes(
            extraSmall = RoundedCornerShape(8.dp),
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(22.dp),
            extraLarge = RoundedCornerShape(28.dp)
        ),
        content = content
    )
}



@Composable
fun App() {

    // Uma única instância mantém o JWT do login disponível para todos os CRUDs.
    val apiService = remember {
        ApiService(createHttpClientEngine())
    }

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
                    subclass(Usuarios::class)
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
    val categoriaViewModel = remember(apiService) {
        CategoriaViewModel(
            CategoriaRepositorioRemoto(apiService)
        )
    }
    val produtoViewModel = remember(apiService) {
        ProdutoViewModel(ProdutoRepositorioRemoto(apiService))
    }
    val clienteViewModel = remember(apiService) {
        ClienteViewModel(ClienteRepositorioRemoto(apiService))
    }
    val vendaViewModel = remember(apiService) {
        VendaViewModel(VendaRepositorioRemoto(apiService))
    }
    var clienteSelecionado by remember { mutableStateOf<ClienteResponseApi?>(null) }
    var vendaSelecionada by remember { mutableStateOf<VendaResponseApi?>(null) }

    val rotaAtual = backStack.lastOrNull() as? Rota ?: DashboardRoute

    LojaInteligenteTheme {
        val feedbackHostState = remember { SnackbarHostState() }
        val feedbackScope = rememberCoroutineScope()
        var autenticado by remember {
            mutableStateOf(false)
        }
        var mostrarCadastro by remember { mutableStateOf(false) }
        var mensagemCadastro by remember { mutableStateOf<String?>(null) }
        var emailSessao by remember { mutableStateOf<String?>(null) }
        var administrador by remember { mutableStateOf(false) }

        LaunchedEffect(autenticado, emailSessao) {
            if (autenticado && emailSessao != null) {
                apiService.listarUsuarios().onSuccess { usuarios ->
                    administrador = usuarios.any {
                        it.email.equals(emailSessao, ignoreCase = true) &&
                            it.perfil.equals("ADMIN", ignoreCase = true)
                    }
                }
            }
        }

        Box(Modifier.fillMaxSize()) {
        if (!autenticado) {
            if (mostrarCadastro) {
                CadastroForm(
                    apiService = apiService,
                    onCadastroSucesso = {
                        mensagemCadastro = "Cadastro realizado com sucesso. Você já pode entrar."
                        mostrarCadastro = false
                    },
                    aoIrParaLogin = { mostrarCadastro = false }
                )
            } else {
                LoginForm(
                    aoEntrar = { email, senha ->
                        apiService.login(email, senha)
                    },
                    aoLoginSucesso = { email ->
                        emailSessao = email
                        autenticado = apiService.possuiToken()
                    },
                    aoIrParaCadastro = { mostrarCadastro = true },
                    mensagemInicial = mensagemCadastro
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Fundo)
            ) {
                MenuLateral(
                    rotaAtual = rotaAtual,
                    administrador = administrador,
                    aoSelecionar = { rota ->
                        while (backStack.size > 1) backStack.removeLastOrNull()
                        if (backStack.lastOrNull() != rota) backStack.add(rota)
                    },
                    aoSair = {
                        apiService.logout()
                        autenticado = false
                        administrador = false
                        emailSessao = null
                        while (backStack.size > 1) backStack.removeLastOrNull()
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
                        Usuarios -> "Usuários"
                        FormularioCategoria -> if (categoriaViewModel.uiState.value.id == null) "Nova categoria" else "Editar categoria"
                        FormularioProduto -> if (produtoViewModel.uiState.value.id == null) "Novo produto" else "Editar produto"
                        FormularioCliente -> if (clienteSelecionado == null) "Novo cliente" else "Editar cliente"
                        FormularioVenda -> if (vendaSelecionada == null) "Nova venda" else "Editar venda"
                        else -> "Módulo"
                    }

                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("KORA  /  GESTÃO COMERCIAL", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextoSecundario, letterSpacing = 1.1.sp)
                            Spacer(Modifier.height(5.dp))
                            Text(tituloTela, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = TextoEscuro)
                            Spacer(Modifier.height(3.dp))
                            Text("Seu centro de controle para a loja inteligente.", color = TextoSecundario, fontSize = 14.sp)
                        }
                        if (rotaAtual in setOf(FormularioCategoria, FormularioProduto, FormularioCliente, FormularioVenda)) {
                            OutlinedButton(onClick = { if (backStack.size > 1) backStack.removeLastOrNull() }) { Text("← Voltar") }
                        }
                    }

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
                                ProdutoLista(
                                    viewModel = produtoViewModel,
                                    atualizarVersao = versaoProdutos,
                                    aoEditar = { produto ->
                                        produtoViewModel.selecionarProduto(produto)
                                        backStack.add(FormularioProduto)
                                    },
                                    aoNovoProduto = {
                                        produtoViewModel.limparFormulario()
                                        backStack.add(FormularioProduto)
                                    }
                                )
                            }

                            entry<FormularioProduto> {
                                ProdutoForm(
                                    apiService = apiService,
                                    viewModel = produtoViewModel,
                                    onProdutoCadastrado = { mensagem ->
                                        feedbackScope.launch { feedbackHostState.showSnackbar(mensagem) }
                                        versaoProdutos++
                                        versaoDashboard++
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }


                            entry<Clientes> {
                                ClienteLista(
                                    viewModel = clienteViewModel,
                                    atualizarVersao = versaoClientes,
                                    aoEditar = { cliente ->
                                        clienteSelecionado = cliente
                                        backStack.add(FormularioCliente)
                                    },
                                    aoNovoCliente = {
                                        clienteSelecionado = null
                                        backStack.add(FormularioCliente)
                                    }
                                )
                            }

                            entry<FormularioCliente> {
                                ClienteForm(
                                    viewModel = clienteViewModel,
                                    cliente = clienteSelecionado,
                                    onClienteCadastrado = { mensagem ->
                                        feedbackScope.launch { feedbackHostState.showSnackbar(mensagem) }
                                        versaoClientes++
                                        versaoDashboard++
                                        clienteSelecionado = null
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }

                            entry<Vendas> {
                                VendaLista(
                                    viewModel = vendaViewModel,
                                    atualizarVersao = versaoVendas,
                                    aoEditar = { venda ->
                                        vendaSelecionada = venda
                                        backStack.add(FormularioVenda)
                                    },
                                    aoNovaVenda = {
                                        vendaSelecionada = null
                                        backStack.add(FormularioVenda)
                                    }
                                )
                            }

                            entry<FormularioVenda> {
                                VendaForm(
                                    apiService = apiService,
                                    viewModel = vendaViewModel,
                                    vendaInicial = vendaSelecionada,
                                    onVendaCadastrada = { mensagem ->
                                        feedbackScope.launch { feedbackHostState.showSnackbar(mensagem) }
                                        versaoVendas++
                                        versaoDashboard++
                                        vendaSelecionada = null
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }

                            entry<Usuarios> {
                                if (administrador) GerenciarUsuarios(apiService)
                                else Text("A gestão de vendedores está disponível somente para administradores.")
                            }

                            entry<ListaCategorias> {
                                TelaEmConstrucao("Lista de Categorias")
                            }

                            entry<FormularioCategoria> {
                                CategoriaForm(
                                    viewModel = categoriaViewModel,
                                    onCategoriaCadastrada = { mensagem ->
                                        feedbackScope.launch { feedbackHostState.showSnackbar(mensagem) }
                                        versaoCategorias++
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }

                            entry<ListaProdutos> {
                                TelaEmConstrucao("Lista de Produtos")
                            }

                            entry<ListaClientes> {
                                TelaEmConstrucao("Lista de Clientes")
                            }

                            entry<ListaVendas> {
                                TelaEmConstrucao("Lista de Vendas")
                            }

                        }
                    )
                }
            }
        }
        SnackbarHost(
            hostState = feedbackHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
            snackbar = { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = Color(0xFF202A3A),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(14.dp)
                )
            }
        )
        }
    }
}


@Composable
private fun MenuLateral(
    rotaAtual: Rota,
    administrador: Boolean,
    aoSelecionar: (Rota) -> Unit,
    aoSair: () -> Unit
) {
    val opcoes = buildList {
        addAll(listOf(
            "Dashboard" to DashboardRoute,
            "Produtos" to Produtos,
            "Categorias" to Categorias,
            "Clientes" to Clientes,
            "Vendas" to Vendas
        ))
        if (administrador) add("Usuários" to Usuarios)
    }
    val rotaSelecionada = when (rotaAtual) {
        FormularioCategoria, ListaCategorias -> Categorias
        FormularioProduto, ListaProdutos -> Produtos
        FormularioCliente, ListaClientes -> Clientes
        FormularioVenda, ListaVendas -> Vendas
        else -> rotaAtual
    }

    BoxWithConstraints(Modifier.fillMaxHeight()) {
        val compacto = maxWidth < 760.dp
        Column(
            modifier = Modifier
                .width(if (compacto) 64.dp else 248.dp)
                .fillMaxHeight()
                .background(AzulEscuro)
                .padding(if (compacto) 9.dp else 18.dp),
            horizontalAlignment = if (compacto) Alignment.CenterHorizontally else Alignment.Start
        ) {
            if (compacto) {
                Surface(shape = RoundedCornerShape(15.dp), color = Azul) {
                    Text("K", modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                }
                Spacer(Modifier.height(26.dp))
            } else {
                Text("KORA", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text("GESTÃO COMERCIAL", fontSize = 10.sp, color = Color(0xFFB9C9E7), letterSpacing = 1.2.sp)
                Spacer(Modifier.height(30.dp))
                Text("MENU PRINCIPAL", fontSize = 10.sp, color = Color(0xFF8194B9), letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
            }

            opcoes.forEach { (nome, rota) ->
                val selecionado = rotaSelecionada == rota
                val rotuloCompacto = when (rota) {
                    DashboardRoute -> "Início"
                    Produtos -> "Prod."
                    Categorias -> "Categ."
                    Clientes -> "Cli."
                    Vendas -> "Vendas"
                    Usuarios -> "Usuários"
                    else -> nome.take(5)
                }
                Surface(
                    onClick = { aoSelecionar(rota) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = if (compacto) 4.dp else 3.dp),
                    shape = RoundedCornerShape(if (compacto) 14.dp else 12.dp),
                    color = if (selecionado) Azul else Color.Transparent
                ) {
                    if (compacto) {
                        Column(
                            modifier = Modifier.padding(vertical = 9.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(nome.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(rotuloCompacto, color = Color(0xFFDDE6F7), fontSize = 8.sp, maxLines = 1, overflow = TextOverflow.Clip)
                        }
                    } else {
                        Row(
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 13.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = CircleShape, color = if (selecionado) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.08f)) {
                                Text(nome.take(1), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                nome,
                                color = Color.White,
                                fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Surface(
                onClick = aoSair,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF8B292E)
            ) {
                if (compacto) {
                    Column(
                        modifier = Modifier.padding(vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text("↪", color = Color.White, fontSize = 17.sp)
                        Text("Sair", color = Color.White, fontSize = 8.sp)
                    }
                } else {
                    Text("Sair da conta", modifier = Modifier.padding(horizontal = 13.dp, vertical = 12.dp), color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
            if (!compacto) {
                Spacer(Modifier.height(12.dp))
                Text("Sistema de Controle", color = Color(0xFFB9C9E7), fontSize = 11.sp)
            }
        }
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
    var leituraPrateleira by remember { mutableStateOf<LeituraPrateleiraApi?>(null) }
    var erroLeituraPrateleira by remember { mutableStateOf(false) }

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

    LaunchedEffect(apiService) {
        while (true) {
            apiService.obterLeituraPrateleira()
                .onSuccess {
                    leituraPrateleira = it
                    erroLeituraPrateleira = false
                }
                .onFailure {
                    leituraPrateleira = null
                    erroLeituraPrateleira = true
                }
            delay(2500)
        }
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

                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val metricas = listOf(
                        Triple("Produtos", totalProdutos, "Total cadastrado"),
                        Triple("Categorias", totalCategorias, "Categorias registradas"),
                        Triple("Usuários", totalUsuarios, "Usuários cadastrados"),
                        Triple("Clientes", totalClientes, "Total cadastrado"),
                        Triple("Vendas", totalVendas, "Movimentações")
                    )
                    if (maxWidth < 600.dp) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            metricas.forEach { (titulo, valor, descricao) ->
                                Indicador(titulo, valor, descricao, Modifier.fillMaxWidth())
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            metricas.chunked(2).forEach { linha ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    linha.forEach { (titulo, valor, descricao) ->
                                        Indicador(titulo, valor, descricao, Modifier.weight(1f))
                                    }
                                    if (linha.size == 1) Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                val leituraAtual = leituraPrateleira
                val possuiLeitura = leituraAtual?.distancia != null || leituraAtual?.ocupacao != null
                val ocupacao = leituraAtual?.ocupacao?.toInt()?.let { "$it%" } ?: "—"
                val distancia = leituraAtual?.distancia?.let { "${(it * 10).toInt() / 10.0} cm" }
                val descricaoLeitura = when {
                    erroLeituraPrateleira -> "Não foi possível consultar o backend IoT"
                    !possuiLeitura -> "Aguardando leitura do sensor"
                    leituraAtual?.online == false -> "Última leitura · sensor offline${distancia?.let { " · $it" }.orEmpty()}"
                    leituraAtual?.online == true -> "Sensor online${distancia?.let { " · distância: $it" }.orEmpty()}"
                    else -> "Leitura recebida${distancia?.let { " · distância: $it" }.orEmpty()}"
                }
                Indicador(
                    titulo = "Ocupação da prateleira",
                    valor = ocupacao,
                    descricao = descricaoLeitura,
                    modifier = Modifier.fillMaxWidth(),
                    progresso = leituraAtual?.ocupacao?.toFloat()?.div(100f)?.coerceIn(0f, 1f)
                )
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
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AzulBorda.copy(alpha = 0.55f)),
                        shadowElevation = 0.5.dp
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
    modifier: Modifier = Modifier,
    progresso: Float? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, AzulBorda.copy(alpha = 0.6f)),
        shadowElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
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
                fontSize = 12.sp
            )
            progresso?.let {
                Spacer(Modifier.height(2.dp))
                LinearProgressIndicator(
                    progress = { it },
                    modifier = Modifier.fillMaxWidth().height(7.dp),
                    color = Verde,
                    trackColor = AzulClaro
                )
            }
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
