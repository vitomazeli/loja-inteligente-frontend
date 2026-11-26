package br.com.lojainteligente.frontend

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
sealed interface Rota : NavKey

@Serializable
data object DashboardRoute : Rota

@Serializable
data object Produtos : Rota

@Serializable
data object Categorias : Rota

@Serializable
data object Clientes : Rota

@Serializable
data object Vendas : Rota


@Serializable
data object ListaCategorias : Rota

@Serializable
data object FormularioCategoria : Rota

@Serializable
data object ListaProdutos : Rota

@Serializable
data object FormularioProduto : Rota

@Serializable
data object ListaClientes : Rota

@Serializable
data object FormularioCliente : Rota

@Serializable
data object ListaVendas : Rota

@Serializable
data object FormularioVenda : Rota