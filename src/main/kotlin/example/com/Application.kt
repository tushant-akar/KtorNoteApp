package example.com

import example.com.plugins.*
import example.com.repository.DatabaseFactory
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
