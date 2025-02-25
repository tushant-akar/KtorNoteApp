package example.com.auth

import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val hashKey = System.getenv("HASH_SECRET_KEY").toByteArray()
private val hmacKey = SecretKeySpec(hashKey, "HmacSHA256")

fun hash(password: String): String {
    val hmac = Mac.getInstance("HmacSHA256")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}