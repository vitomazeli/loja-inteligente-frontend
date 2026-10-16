package br.com.lojainteligente.frontend

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform