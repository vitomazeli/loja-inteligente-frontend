
package br.com.lojainteligente.frontend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.ApiService
import br.com.lojainteligente.frontend.data.CategoriaResponseApi
import kotlinx.coroutines.launch

@Composable
fun CategoriaLista(
    apiService: ApiService,
    modifier: Modifier = Modifier
) {
    var categorias by remember {
        mutableStateOf<List<CategoriaResponseApi>>(emptyList())
    }

    var carregando by remember {
        mutableStateOf(true)
    }

    var mensagem by remember {
        mutableStateOf<String?>(null)
    }

    var erro by remember {
        mutableStateOf(false)
    }

    var versaoLocal by remember {
        mutableStateOf(0)
    }

    val scope = rememberCoroutineScope()

    suspend fun carregarCategorias() {
        carregando = true
        mensagem = null

        apiService.listarCategorias().fold(
            onSuccess = { lista ->
                categorias = lista
                erro = false
            },
            onFailure = { falha ->
                mensagem = falha.message
                    ?: "Não foi possível carregar as categorias."
                erro = true
            }
        )

        carregando = false
    }

    LaunchedEffect(apiService, versaoLocal) {
        carregarCategorias()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Categorias cadastradas",
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                onClick = {
                    scope.launch {
                        carregarCategorias()
                    }
                },
                enabled = !carregando
            ) {
                Text("Atualizar")
            }
        }

        when {
            carregando -> {
                CircularProgressIndicator()
            }

            erro -> {
                Text(
                    text = mensagem
                        ?: "Erro ao carregar categorias.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            categorias.isEmpty() -> {
                Text("Nenhuma categoria cadastrada.")
            }

            else -> {
                Text("Total de categorias: ${categorias.size}")

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = categorias,
                        key = { categoria ->
                            categoria.id
                                ?: "${categoria.nome}-${categoria.ordemExibicao}"
                        }
                    ) { categoria ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = categoria.nome,
                                    style =
                                        MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = categoria.descricao
                                        ?: "Sem descrição"
                                )

                                Text(
                                    text = "ID: ${categoria.id ?: "—"}"
                                )

                                Text(
                                    text = "Status: ${
                                        if (categoria.ativo) {
                                            "Ativa"
                                        } else {
                                            "Inativa"
                                        }
                                    }"
                                )

                                Text(
                                    text = "Ordem de exibição: " +
                                            categoria.ordemExibicao
                                )

                                Text(
                                    text = "Cadastrada em: ${
                                        formatarDataCadastro(
                                            categoria.dataCadastro
                                        )
                                    }",
                                    style =
                                        MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme
                                        .onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}

private fun formatarDataCadastro(data: String): String {
    if (data.isBlank()) {
        return "Data não disponível"
    }

    return try {
        val dataHora = data.substringBefore(".")
        val partes = dataHora.split("T")

        if (partes.size != 2) {
            data
        } else {
            val dataPartes = partes[0].split("-")

            if (dataPartes.size != 3) {
                data
            } else {
                val hora = partes[1].take(5)

                "${dataPartes[2]}/${dataPartes[1]}/${dataPartes[0]} $hora"
            }
        }
    } catch (e: Exception) {
        data
    }
}
