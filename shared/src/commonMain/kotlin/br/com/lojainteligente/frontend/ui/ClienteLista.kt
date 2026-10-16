
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
import kotlinx.coroutines.launch
import br.com.lojainteligente.frontend.data.ApiService
import br.com.lojainteligente.frontend.model.ClienteResponseApi
import androidx.compose.ui.graphics.Color

@Composable
fun ClienteLista(
    apiService: ApiService,
    atualizarVersao: Int = 0
) {
    var clientes by remember {
        mutableStateOf<List<ClienteResponseApi>>(emptyList())
    }

    var carregando by remember {
        mutableStateOf(false)
    }

    var mensagem by remember {
        mutableStateOf("")
    }

    var versaoLocal by remember {
        mutableStateOf(0)
    }

    val scope = rememberCoroutineScope()

    fun carregarClientes() {
        scope.launch {
            carregando = true
            mensagem = ""

            apiService.listarClientes()
                .onSuccess {
                    clientes = it
                }
                .onFailure {
                    mensagem = "Erro ao carregar clientes: ${it.message}"
                }

            carregando = false
        }
    }

    LaunchedEffect(apiService, atualizarVersao, versaoLocal) {
        carregarClientes()
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
                text = "Clientes cadastrados",
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = { versaoLocal++ },
                enabled = !carregando
            ) {
                Text("Atualizar")
            }
        }

        if (carregando) {
            CircularProgressIndicator()
        }

        if (!carregando && clientes.isEmpty() && mensagem.isBlank()) {
            Text("Nenhum cliente cadastrado.")
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
                items = clientes,
                key = { it.id }
            ) { cliente ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                }
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = cliente.nome,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("E-mail: ${cliente.email}")

                        Text(
                            "Telefone: ${cliente.telefone ?: "Não informado"}"
                        )

                        Text(
                            "Status: ${if (cliente.ativo) "Ativo" else "Inativo"}"
                        )

                        Text(
                            text = "Cadastrado em: ${
                                formatarDataCadastro(cliente.dataCadastro)
                            }",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
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
            val hora = partes[1]

            if (dataPartes.size != 3) {
                data
            } else {
                "${dataPartes[2]}/${dataPartes[1]}/${dataPartes[0]} " +
                        hora.take(5)
            }
        }
    } catch (e: Exception) {
        data
    }
}
