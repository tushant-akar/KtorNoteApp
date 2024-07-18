package example.com.plugins

import example.com.auth.JWTService
import example.com.auth.hash
import example.com.repository.DatabaseRepository
import example.com.routes.NoteRoutes
import example.com.routes.UserRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val db = DatabaseRepository()
    val jwtService = JWTService()
    val hashFunction = { s: String -> hash(s) }

    routing {
        get("/") {
            call.respondText("Hello World!", ContentType.Text.Plain)
        }
        UserRoutes(db = db, jwtService = jwtService, hashFunction = hashFunction)
        NoteRoutes(db = db, jwtService = jwtService, hashFunction = hashFunction)
    }
}
