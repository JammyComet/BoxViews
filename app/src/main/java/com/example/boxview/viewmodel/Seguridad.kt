package com.example.boxview.viewmodel

// Lógica: Genera y valida hashes de contraseñas.
import android.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// hash de contraseñas
internal fun hashClave(clave: String, sal: ByteArray): String {
    val spec = PBEKeySpec(clave.toCharArray(), sal, 100_000, 256)
    return try { Base64.encodeToString(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        .generateSecret(spec).encoded, Base64.NO_WRAP) }
    finally { spec.clearPassword() }
}
