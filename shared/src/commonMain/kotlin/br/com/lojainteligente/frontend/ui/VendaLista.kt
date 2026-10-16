
package br.com.lojainteligente.frontend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lojainteligente.frontend.data.ApiService
import br.com.lojainteligente.frontend.model.VendaResponseApi

@Composable
fun VendaLista(
    apiService: ApiService,
    atualizarVersao: Int = 0
) {
    var vendas by remember {
        mutableStateOf<List<VendaResponseApi>>(emptyList())
    }

    var carregando by remember {
        mutableStateOf(false)
    }

    var mensagem by remember {
        mutableStateOf("")
    }

    var versao by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(apiService, atualizarVersao, versao) {
        carregando = true
        mensagem = ""

        apiService.listarVendas()
            .onSuccess {
                vendas = it
            }
            .onFailure {
                mensagem = "Erro ao carregar vendas: ${it.message}"
            }

        carregando = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Vendas realizadas",
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                onClick = { versao++ },
                enabled = !carregando
            ) {
                Text("Atualizar")
            }
        }

        if (carregando) {
            CircularProgressIndicator()
        }

        if (!carregando && vendas.isEmpty() && mensagem.isBlank()) {
            Text("Nenhuma venda cadastrada.")
        }

        if (mensagem.isNotBlank()) {
            Text(
                text = mensagem,
                color = MaterialTheme.colorScheme.error
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = vendas,
                key = { venda ->
                    venda.id?.toString()
                        ?: "venda-sem-id-${vendas.indexOf(venda)}"
                }
            ) { venda ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Venda #${venda.id ?: "Não informado"}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text("Cliente: ${venda.clienteNome}")

                        Text(
                            text = "Data: ${
                                formatarDataVenda(venda.dataVenda ?: "")
                            }",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Total: ${formatarMoeda(venda.total)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text("Status: ${venda.status}")

                        if (!venda.observacao.isNullOrBlank()) {
                            Text("Observação: ${venda.observacao}")
                        }

                        Text(
                            text = "Itens da venda",
                            style = MaterialTheme.typography.titleSmall
                        )

                        venda.itens.forEach { item ->
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(item.produtoNome ?: "Produto não informado")
                                Text("Quantidade: ${item.quantidade}")

                                Text(
                                    "Preço unitário: ${
                                        formatarMoeda(item.precoUnitario)
                                    }"
                                )

                                Text(
                                    "Subtotal: ${formatarMoeda(item.subtotal)}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatarDataVenda(data: String): String {
    if (data.isBlank()) return "Data não disponível"

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
                "${dataPartes[2]}/${dataPartes[1]}/${dataPartes[0]} " +
                        partes[1].take(5)
            }
        }
    } catch (e: Exception) {
        data
    }
}

private fun formatarMoeda(valor: Double): String {
    val centavos = kotlin.math.round(valor * 100).toLong()
    val inteiro = centavos / 100
    val decimal = kotlin.math.abs(centavos % 100)

    return "R$ $inteiro,${decimal.toString().padStart(2, '0')}"
}
