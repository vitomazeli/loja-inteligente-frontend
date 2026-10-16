package br.com.lojainteligente.frontend.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

actual fun createHttpClientEngine(): HttpClientEngine =
    CIO.create()