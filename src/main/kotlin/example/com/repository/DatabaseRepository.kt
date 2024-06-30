package example.com.repository

import example.com.data.model.Note
import example.com.data.model.User
import example.com.data.table.NoteTable
import example.com.data.table.UserTable
import example.com.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class DatabaseRepository {
    suspend fun addUser(user: User) {
        dbQuery {
            UserTable.insert { ut ->
                ut[email] = user.email
                ut[name] = user.userName
                ut[hashPassword] = user.hashPassword
            }
        }
    }

    suspend fun findUserByEmail(email: String) = dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }
        return User(
            email = row[UserTable.email],
            userName = row[UserTable.name],
            hashPassword = row[UserTable.hashPassword]
        )
    }


    suspend fun addNote(note: Note, email: String) {
        dbQuery {
            NoteTable.insert { nt ->
                nt[id] = note.id
                nt[userEmail] = email
                nt[noteTitle] = note.noteTitle
                nt[description] = note.description
                nt[date] = note.date
            }
        }
    }

    suspend fun getAllNotes(email: String): List<Note> = dbQuery {
        NoteTable.select {
            NoteTable.userEmail.eq(email)
        }.mapNotNull {
            rowToNote(it)
        }
    }

    suspend fun updateNote(note: Note, email: String) {
        dbQuery {
            NoteTable.update(
                where = {
                    NoteTable.userEmail.eq(email) and NoteTable.id.eq(note.id)
                }
            ) { nt ->
                nt[noteTitle] = note.noteTitle
                nt[description] = note.description
                nt[date] = note.date
            }
        }
    }

    suspend fun deleteNote(id: String, email: String) {
        dbQuery {
            NoteTable.deleteWhere { NoteTable.id.eq(id) and NoteTable.userEmail.eq(email) }
        }
    }

    private fun rowToNote(row: ResultRow): Note {
        return Note(
            id = row[NoteTable.id],
            noteTitle = row[NoteTable.noteTitle],
            description = row[NoteTable.description],
            date = row[NoteTable.date]
        )
    }
}