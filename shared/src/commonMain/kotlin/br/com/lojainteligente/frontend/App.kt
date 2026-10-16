
package br.com.lojainteligente.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.lojainteligente.frontend.data.ApiService
import br.com.lojainteligente.frontend.data.CategoriaRequestApi
import br.com.lojainteligente.frontend.data.createHttpClientEngine
import br.com.lojainteligente.frontend.ui.CadastroForm
import br.com.lojainteligente.frontend.ui.CategoriaForm
import br.com.lojainteligente.frontend.ui.CategoriaLista
import br.com.lojainteligente.frontend.ui.ClienteForm
import br.com.lojainteligente.frontend.ui.ClienteLista
import br.com.lojainteligente.frontend.ui.LoginForm
import br.com.lojainteligente.frontend.ui.ProdutoForm
import br.com.lojainteligente.frontend.ui.ProdutoLista
import br.com.lojainteligente.frontend.ui.VendaForm
import br.com.lojainteligente.frontend.ui.VendaLista

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

    LojaInteligenteTheme {
        val apiService = remember {
            ApiService(createHttpClientEngine())
        }

        var autenticado by remember {
            mutableStateOf(false)
        }

        var telaSelecionada by remember {
            mutableStateOf("Dashboard")
        }

        if (!autenticado) {
            LoginForm(
                aoEntrar = { email, senha ->
                    apiService.login(email, senha)
                },
                aoLoginSucesso = {
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
                    telaSelecionada = telaSelecionada,
                    aoSelecionar = { tela ->
                        telaSelecionada = tela
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(24.dp)
                ) {
                    val tituloTela = when (telaSelecionada) {
                        "Dashboard" -> "Visão geral"
                        else -> telaSelecionada
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

                    when (telaSelecionada) {

                        "Dashboard" -> Dashboard(
                            apiService = apiService,
                            atualizarVersao = versaoDashboard,
                            aoSelecionar = {
                                telaSelecionada = it
                            }
                        )

                        "Categorias" -> Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CategoriaForm(
                                aoGravar = { categoria ->
                                    apiService.cadastrarCategoria(
                                        CategoriaRequestApi(
                                            nome = categoria.nome,
                                            descricao = categoria.descricao,
                                            ativo = categoria.ativo,
                                            ordemExibicao = categoria.ordemExibicao
                                        )
                                    ).map { resposta ->
                                        versaoCategorias++
                                        versaoDashboard++

                                        "Categoria '${resposta.nome}' cadastrada com sucesso! ID: ${resposta.id}"
                                    }
                                }
                            )

                            HorizontalDivider()

                            CategoriaLista(
                                apiService = apiService,
                                modifier = Modifier.weight(1f),

                            )
                        }

                        "Usuários" -> CadastroForm(
                            apiService = apiService,
                            onCadastroSucesso = {
                                versaoUsuarios++
                                versaoDashboard++
                            }
                        )

                        "Produtos" -> Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ProdutoForm(
                                apiService = apiService,
                                onProdutoCadastrado = {
                                    versaoProdutos++
                                    versaoDashboard++
                                }
                            )

                            HorizontalDivider()

                            ProdutoLista(
                                apiService = apiService,
                                atualizarVersao = versaoProdutos
                            )
                        }

                        "Clientes" -> Column(
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

                        "Vendas" -> Column(
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

                        else -> TelaEmConstrucao(telaSelecionada)
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuLateral(
    telaSelecionada: String,
    aoSelecionar: (String) -> Unit
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
            "Dashboard",
            "Produtos",
            "Categorias",
            "Usuários",
            "Clientes",
            "Vendas"
        )

        opcoes.forEach { opcao ->
            val selecionado = telaSelecionada == opcao

            Surface(
                onClick = { aoSelecionar(opcao) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (selecionado) Azul else Color.Transparent
            ) {
                Text(
                    text = opcao,
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
    aoSelecionar: (String) -> Unit
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
                    "Produtos",
                    "Categorias",
                    "Usuários",
                    "Clientes",
                    "Vendas"
                ).forEach { modulo ->
                    Surface(
                        onClick = { aoSelecionar(modulo) },
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
