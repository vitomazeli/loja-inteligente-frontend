
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
import br.com.lojainteligente.frontend.ui.components.KoraMetricCard
import br.com.lojainteligente.frontend.ui.components.KoraMetricSymbol
import br.com.lojainteligente.frontend.ui.components.KoraPageHeader
import br.com.lojainteligente.frontend.ui.components.KoraStatusBadge
import br.com.lojainteligente.frontend.ui.components.KoraStatusTone


import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.remember
import br.com.lojainteligente.frontend.resources.Res
import br.com.lojainteligente.frontend.resources.inter_variable
import org.jetbrains.compose.resources.Font
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



private val AzulEscuro = Color(0xFF0B1F42)
private val Azul = Color(0xFF2563EB)
private val AzulSecundario = Color(0xFF3882F6)
private val Fundo = Color(0xFFF4F7FC)
private val TextoEscuro = Color(0xFF17243B)
private val TextoSecundario = Color(0xFF64748B)
private val AzulClaro = Color(0xFFEFF6FF)
private val AzulBorda = Color(0xFFE2E8F0)
private val Sucesso = Color(0xFF22C55E)
private val Aviso = Color(0xFFF59E0B)
private val Erro = Color(0xFFEF4444)

private val CoresLojaInteligente = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulEscuro,

    secondary = AzulSecundario,
    onSecondary = Color.White,
    secondaryContainer = AzulClaro,
    onSecondaryContainer = AzulEscuro,

    tertiary = Sucesso,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDCFCE7),
    onTertiaryContainer = Color(0xFF14532D),

    background = Fundo,
    onBackground = TextoEscuro,

    surface = Color.White,
    onSurface = TextoEscuro,

    surfaceVariant = AzulClaro,
    onSurfaceVariant = TextoSecundario,

    outline = AzulBorda,
    error = Erro,
    onError = Color.White,
    errorContainer = Color(0xFFFEF2F2),
    onErrorContainer = Color(0xFF7F1D1D)
)

@Composable
private fun LojaInteligenteTheme(
    content: @Composable () -> Unit
) {
    val inter = FontFamily(
        Font(Res.font.inter_variable, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.inter_variable, FontWeight.Medium, FontStyle.Normal),
        Font(Res.font.inter_variable, FontWeight.SemiBold, FontStyle.Normal),
        Font(Res.font.inter_variable, FontWeight.Bold, FontStyle.Normal)
    )
    val baseTypography = Typography()
    val typography = baseTypography.copy(
        displayLarge = baseTypography.displayLarge.copy(fontFamily = inter, fontWeight = FontWeight.Bold),
        displayMedium = baseTypography.displayMedium.copy(fontFamily = inter, fontWeight = FontWeight.Bold),
        displaySmall = baseTypography.displaySmall.copy(fontFamily = inter, fontWeight = FontWeight.Bold),
        headlineLarge = baseTypography.headlineLarge.copy(fontFamily = inter, fontWeight = FontWeight.Bold),
        headlineMedium = baseTypography.headlineMedium.copy(fontFamily = inter, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 36.sp),
        headlineSmall = baseTypography.headlineSmall.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp),
        titleLarge = baseTypography.titleLarge.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
        titleMedium = baseTypography.titleMedium.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
        titleSmall = baseTypography.titleSmall.copy(fontFamily = inter, fontWeight = FontWeight.Medium),
        bodyLarge = baseTypography.bodyLarge.copy(fontFamily = inter, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
        bodyMedium = baseTypography.bodyMedium.copy(fontFamily = inter, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = baseTypography.bodySmall.copy(fontFamily = inter, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp),
        labelLarge = baseTypography.labelLarge.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
        labelMedium = baseTypography.labelMedium.copy(fontFamily = inter, fontWeight = FontWeight.Medium),
        labelSmall = baseTypography.labelSmall.copy(fontFamily = inter, fontWeight = FontWeight.Medium)
    )
    MaterialTheme(
        colorScheme = CoresLojaInteligente,
        typography = typography,
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
                        DashboardRoute -> "Visão Geral"
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

                    val descricaoTela = when (rotaAtual) {
                        DashboardRoute -> "Acompanhe os principais indicadores e a leitura da prateleira."
                        Produtos -> "Gerencie os produtos disponíveis na loja."
                        Categorias -> "Organize os produtos em categorias."
                        Clientes -> "Consulte e mantenha os dados dos clientes."
                        Vendas -> "Registre e acompanhe as vendas da loja."
                        Usuarios -> "Gerencie o acesso dos vendedores."
                        FormularioCategoria -> if (categoriaViewModel.uiState.value.id == null) "Preencha os dados para cadastrar a categoria." else "Atualize os dados da categoria."
                        FormularioProduto -> if (produtoViewModel.uiState.value.id == null) "Preencha os dados para cadastrar o produto." else "Atualize os dados do produto."
                        FormularioCliente -> if (clienteSelecionado == null) "Preencha os dados para cadastrar o cliente." else "Atualize os dados do cliente."
                        FormularioVenda -> if (vendaSelecionada == null) "Informe os dados para registrar a venda." else "Atualize os dados da venda."
                        else -> "Acesse e gerencie os recursos da loja."
                    }

                    KoraPageHeader(tituloTela, descricaoTela) {
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
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 88.dp, end = 16.dp).widthIn(max = 420.dp),
            snackbar = { snackbarData ->
                val texto = snackbarData.visuals.message
                val sucesso = listOf("sucesso", "atualizado", "cadastrado", "cadastrada", "excluído", "ativado", "desativado")
                    .any { texto.contains(it, ignoreCase = true) }
                val aviso = listOf("aviso", "atenção", "limpos", "limpo")
                    .any { texto.contains(it, ignoreCase = true) }
                val corFundo = when {
                    sucesso -> Color(0xFF166534)
                    aviso -> Color(0xFFFEF3C7)
                    else -> MaterialTheme.colorScheme.errorContainer
                }
                val corTexto = when {
                    sucesso -> Color.White
                    aviso -> Color(0xFF92400E)
                    else -> MaterialTheme.colorScheme.onErrorContainer
                }
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = corFundo,
                    contentColor = corTexto,
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
            "Visão Geral" to DashboardRoute,
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
                    DashboardRoute -> "Visão"
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
                            IconeNavegacao(rota, Modifier.size(20.dp))
                            Text(rotuloCompacto, color = Color(0xFFDDE6F7), fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Clip)
                        }
                    } else {
                        Row(
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 13.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = CircleShape, color = if (selecionado) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.08f)) {
                                IconeNavegacao(rota, Modifier.padding(7.dp).size(18.dp))
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
                color = Erro
            ) {
                if (compacto) {
                    Column(
                        modifier = Modifier.padding(vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        IconeSair(Modifier.size(19.dp))
                        Text("Sair", color = Color.White, fontSize = 9.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconeSair(Modifier.size(18.dp))
                        Text("Sair", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
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
private fun IconeSair(
    modifier: Modifier = Modifier,
    cor: Color = Color.White
) {
    Canvas(modifier) {
        val lado = size.minDimension
        val espessura = lado * 0.09f
        fun ponto(x: Float, y: Float) = Offset(lado * x, lado * y)

        drawLine(cor, ponto(0.57f, 0.16f), ponto(0.57f, 0.84f), espessura, cap = StrokeCap.Round)
        drawLine(cor, ponto(0.12f, 0.50f), ponto(0.48f, 0.50f), espessura, cap = StrokeCap.Round)
        drawLine(cor, ponto(0.35f, 0.36f), ponto(0.49f, 0.50f), espessura, cap = StrokeCap.Round)
        drawLine(cor, ponto(0.35f, 0.64f), ponto(0.49f, 0.50f), espessura, cap = StrokeCap.Round)
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
                    text = "Indicadores da loja",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextoEscuro
                )

                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val metricas = listOf(
                        KoraMetricSymbol.Products to Triple("Produtos", totalProdutos, "Total cadastrado"),
                        KoraMetricSymbol.Categories to Triple("Categorias", totalCategorias, "Categorias registradas"),
                        KoraMetricSymbol.Users to Triple("Usuários", totalUsuarios, "Usuários cadastrados"),
                        KoraMetricSymbol.Customers to Triple("Clientes", totalClientes, "Total cadastrado"),
                        KoraMetricSymbol.Sales to Triple("Vendas", totalVendas, "Movimentações")
                    )
                    val colunas = when {
                        maxWidth >= 900.dp -> 3
                        maxWidth >= 560.dp -> 2
                        else -> 1
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        metricas.chunked(colunas).forEach { linha ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                linha.forEach { (icone, indicador) ->
                                    val (titulo, valor, descricao) = indicador
                                    KoraMetricCard(titulo, valor, descricao, icone, Modifier.weight(1f))
                                }
                                repeat(colunas - linha.size) { Spacer(Modifier.weight(1f)) }
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
                CardOcupacaoPrateleira(
                    valor = ocupacao,
                    descricao = descricaoLeitura,
                    progresso = leituraAtual?.ocupacao?.toFloat()?.div(100f)?.coerceIn(0f, 1f),
                    statusSensor = when {
                        erroLeituraPrateleira -> "Sem conexão" to KoraStatusTone.Danger
                        leituraAtual == null -> "Aguardando" to KoraStatusTone.Neutral
                        leituraAtual.online == false -> "Offline" to KoraStatusTone.Warning
                        leituraAtual.online == true -> "Online" to KoraStatusTone.Success
                        else -> "Recebendo dados" to KoraStatusTone.Primary
                    }
                )
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Acesso rápido",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AzulEscuro
                )

                val acessos = listOf(
                    "Produtos" to Produtos,
                    "Categorias" to Categorias,
                    "Clientes" to Clientes,
                    "Vendas" to Vendas
                )
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val colunas = if (maxWidth >= 500.dp) 2 else 1
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        acessos.chunked(colunas).forEach { linha ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                linha.forEach { (modulo, rota) ->
                                    CardAcessoRapido(modulo, rota, { aoSelecionar(rota) }, Modifier.weight(1f))
                                }
                                repeat(colunas - linha.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardOcupacaoPrateleira(
    valor: String,
    descricao: String,
    progresso: Float?,
    statusSensor: Pair<String, KoraStatusTone>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, AzulBorda.copy(alpha = 0.75f)),
        shadowElevation = 1.dp
    ) {
        BoxWithConstraints {
            if (maxWidth < 500.dp) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Ocupação da prateleira", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulEscuro)
                        KoraStatusBadge(statusSensor.first, statusSensor.second)
                    }
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(valor, fontSize = 42.sp, lineHeight = 46.sp, fontWeight = FontWeight.Bold, color = Azul)
                        Text("da capacidade", modifier = Modifier.padding(bottom = 5.dp), style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    }
                    Text(descricao, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    progresso?.let {
                        LinearProgressIndicator(
                            progress = { it },
                            modifier = Modifier.fillMaxWidth().height(10.dp),
                            color = Azul,
                            trackColor = AzulClaro
                        )
                    }
                }
            } else {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Ocupação da prateleira", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AzulEscuro)
                            KoraStatusBadge(statusSensor.first, statusSensor.second)
                        }
                        Text(descricao, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                        progresso?.let {
                            LinearProgressIndicator(
                                progress = { it },
                                modifier = Modifier.fillMaxWidth().height(10.dp),
                                color = Azul,
                                trackColor = AzulClaro
                            )
                        }
                    }
                    Text(valor, fontSize = 48.sp, lineHeight = 54.sp, fontWeight = FontWeight.Bold, color = Azul)
                }
            }
        }
    }
}

@Composable
private fun CardAcessoRapido(
    titulo: String,
    rota: Rota,
    aoAbrir: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = aoAbrir,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, AzulBorda.copy(alpha = 0.65f)),
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Surface(modifier = Modifier.size(42.dp), shape = RoundedCornerShape(14.dp), color = AzulClaro) {
                Box(contentAlignment = Alignment.Center) {
                    IconeNavegacao(rota, Modifier.size(22.dp), cor = Azul)
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AzulEscuro)
                Text("Abrir módulo", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
            }
            Text("→", color = Azul, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun IconeNavegacao(
    rota: Rota,
    modifier: Modifier = Modifier,
    cor: Color = Color.White
) {
    Canvas(modifier) {
        val lado = size.minDimension
        val espessura = lado * 0.09f
        val pincel = Stroke(width = espessura, cap = StrokeCap.Round)
        fun ponto(x: Float, y: Float) = Offset(lado * x, lado * y)

        when (rota) {
            DashboardRoute -> {
                val tile = lado * 0.29f
                val raio = CornerRadius(lado * 0.05f, lado * 0.05f)
                listOf(
                    Offset(lado * 0.13f, lado * 0.13f), Offset(lado * 0.58f, lado * 0.13f),
                    Offset(lado * 0.13f, lado * 0.58f), Offset(lado * 0.58f, lado * 0.58f)
                ).forEach { origem ->
                    drawRoundRect(cor, origem, Size(tile, tile), raio, style = pincel)
                }
            }
            Produtos -> {
                val caixa = Path().apply {
                    moveTo(lado * 0.12f, lado * 0.29f); lineTo(lado * 0.50f, lado * 0.10f)
                    lineTo(lado * 0.88f, lado * 0.29f); lineTo(lado * 0.88f, lado * 0.72f)
                    lineTo(lado * 0.50f, lado * 0.91f); lineTo(lado * 0.12f, lado * 0.72f); close()
                }
                drawPath(caixa, cor, style = pincel)
                drawLine(cor, ponto(0.12f, 0.29f), ponto(0.50f, 0.49f), espessura, cap = StrokeCap.Round)
                drawLine(cor, ponto(0.88f, 0.29f), ponto(0.50f, 0.49f), espessura, cap = StrokeCap.Round)
                drawLine(cor, ponto(0.50f, 0.49f), ponto(0.50f, 0.90f), espessura, cap = StrokeCap.Round)
            }
            Categorias -> {
                val pasta = Path().apply {
                    moveTo(lado * 0.12f, lado * 0.28f); lineTo(lado * 0.42f, lado * 0.28f)
                    lineTo(lado * 0.53f, lado * 0.40f); lineTo(lado * 0.88f, lado * 0.40f)
                    lineTo(lado * 0.82f, lado * 0.78f); lineTo(lado * 0.15f, lado * 0.78f); close()
                }
                drawPath(pasta, cor, style = pincel)
            }
            Clientes -> {
                drawCircle(cor, radius = lado * 0.13f, center = ponto(0.50f, 0.31f), style = pincel)
                drawArc(cor, 180f, 180f, false, Offset(lado * 0.17f, lado * 0.40f), Size(lado * 0.66f, lado * 0.48f), style = pincel)
            }
            Vendas -> {
                drawLine(cor, ponto(0.15f, 0.82f), ponto(0.15f, 0.18f), espessura, cap = StrokeCap.Round)
                drawLine(cor, ponto(0.15f, 0.82f), ponto(0.88f, 0.82f), espessura, cap = StrokeCap.Round)
                val grafico = Path().apply {
                    moveTo(lado * 0.23f, lado * 0.66f); lineTo(lado * 0.42f, lado * 0.49f)
                    lineTo(lado * 0.57f, lado * 0.57f); lineTo(lado * 0.79f, lado * 0.30f)
                }
                drawPath(grafico, cor, style = pincel)
            }
            Usuarios -> {
                drawCircle(cor, radius = lado * 0.11f, center = ponto(0.38f, 0.32f), style = pincel)
                drawCircle(cor, radius = lado * 0.10f, center = ponto(0.69f, 0.37f), style = pincel)
                drawArc(cor, 180f, 180f, false, Offset(lado * 0.10f, lado * 0.43f), Size(lado * 0.55f, lado * 0.42f), style = pincel)
                drawArc(cor, 180f, 180f, false, Offset(lado * 0.48f, lado * 0.49f), Size(lado * 0.40f, lado * 0.33f), style = pincel)
            }
            else -> drawCircle(cor, radius = lado * 0.28f, center = ponto(0.50f, 0.50f), style = pincel)
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
