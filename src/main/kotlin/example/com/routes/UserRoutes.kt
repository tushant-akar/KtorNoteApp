package example.com.routes

import example.com.auth.JWTService
import example.com.data.model.LoginRequest
import example.com.data.model.RegisterRequest
import example.com.data.model.SimpleResponse
import example.com.data.model.User
import example.com.repository.DatabaseRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"

fun Route.UserRoutes(
    db: DatabaseRepository,
    jwtService: JWTService,
    hashFunction: (String) -> String
) {

    post(REGISTER_REQUEST) {
        val registerRequest = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing some fields"))
            return@post
        }

        try {
            val user = User(email = registerRequest.email, userName = registerRequest.name, hashPassword =  hashFunction(registerRequest.password))
            db.addUser(user)
            call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))
        } catch (e: Throwable) {
            call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Error registering"))
        }
    }

    post(LOGIN_REQUEST) {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing some fields"))
            return@post
        }

        try {
            val user = db.findUserByEmail(loginRequest.email)
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Wrong EmailID"))
            } else {
                if (user.hashPassword == hashFunction(loginRequest.password)) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))
                } else {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Wrong password"))
                }
            }
        } catch (e: Throwable) {
            call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Error logging in"))
        }
    }

}