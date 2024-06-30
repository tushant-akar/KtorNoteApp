package example.com.routes

import example.com.auth.JWTService
import example.com.data.model.Note
import example.com.data.model.SimpleResponse
import example.com.data.model.User
import example.com.repository.DatabaseRepository
import io.ktor.client.engine.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val NOTES = "$API_VERSION/notes"
const val CREATE_NOTES = "$NOTES/create"
const val DELETE_NOTES = "$NOTES/delete"
const val UPDATE_NOTES = "$NOTES/update"

fun Route.NoteRoutes(
    db: DatabaseRepository,
    jwtService: JWTService,
    hashFunction: (String) -> String
) {
    authenticate("jwt") {
        post(CREATE_NOTES) {
            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false, "Missing fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                db.addNote(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note added"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Problems retrieving User"))
            }
        }
        post(NOTES) {
            try {
                val email = call.principal<User>()!!.email
                call.respond(HttpStatusCode.OK, db.getAllNotes(email))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Problems retrieving User"))
            }
        }
        put(UPDATE_NOTES) {
            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false, "Missing fields"))
                return@put
            }
            try {
                val email = call.principal<User>()!!.email
                db.updateNote(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note updated"))
            }
            catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Problems retrieving User"))
            }
        }
        delete(DELETE_NOTES) {
            val id = try {
                call.parameters["id"]!!
            }
            catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "QueryParameter: id not present"))
                return@delete
            }

            try {
                val email = call.principal<User>()!!.email
                db.deleteNote(id, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note deleted"))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Problems retrieving User"))
            }
        }
    }
}