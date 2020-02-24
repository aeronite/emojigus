package com.argus3000

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.serialization.serialization

fun main(args: Array<String>) = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        serialization()
    }
}

