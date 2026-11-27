package br.com.lojainteligente.frontend.ui.screens.vendas

internal fun normalizarStatusVenda(status: String?): String? =
    when (status?.trim()?.uppercase()) {
        "PENDENTE" -> "PENDENTE"
        "CONCLUIDA", "CONCLUÍDA", "CONCLUIDO", "CONCLUÍDO" -> "CONCLUIDA"
        else -> null
    }

internal fun rotuloStatusVenda(status: String?): String =
    when (normalizarStatusVenda(status)) {
        "PENDENTE" -> "Pendente"
        "CONCLUIDA" -> "Concluída"
        else -> status?.trim()?.takeIf { it.isNotEmpty() } ?: "Status não informado"
    }
