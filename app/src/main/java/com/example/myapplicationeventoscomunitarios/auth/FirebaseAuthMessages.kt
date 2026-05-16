package com.example.myapplicationeventoscomunitarios.auth

import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

fun Throwable.toFirebaseAuthUserMessage(): String = when (this) {
    is FirebaseAuthWeakPasswordException ->
        "La contraseña es demasiado débil. Usa al menos 6 caracteres."
    is FirebaseAuthInvalidCredentialsException ->
        "Correo o contraseña incorrectos."
    is FirebaseAuthInvalidUserException ->
        "No existe una cuenta con este correo."
    is FirebaseAuthUserCollisionException ->
        "Este correo ya está registrado."
    is FirebaseAuthException -> when (errorCode) {
        "ERROR_CONFIGURATION_NOT_FOUND",
        "CONFIGURATION_NOT_FOUND" ->
            "Firebase Auth no está configurado en el proyecto: en Google Cloud activa la API " +
                "\"Identity Toolkit API\" para el proyecto enlazado a Firebase, y en Firebase Console " +
                "entra a Authentication (inicia el producto si hace falta) y activa el método " +
                "Correo/contraseña."
        "ERROR_INVALID_EMAIL" -> "El correo electrónico no es válido."
        "ERROR_WRONG_PASSWORD" -> "Contraseña incorrecta."
        "ERROR_USER_NOT_FOUND" -> "No existe una cuenta con este correo."
        "ERROR_USER_DISABLED" -> "Esta cuenta ha sido deshabilitada."
        "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Prueba más tarde."
        "ERROR_NETWORK_REQUEST_FAILED" -> "Error de red. Comprueba tu conexión."
        else -> if (localizedMessage?.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) == true) {
            "Firebase Auth no está configurado (CONFIGURATION_NOT_FOUND). Revisa Identity Toolkit API y Authentication en la consola."
        } else {
            localizedMessage ?: "Error de autenticación (${errorCode})."
        }
    }
    is ApiException -> when (statusCode) {
        12501 -> "Inicio de sesión con Google cancelado."
        10 -> "Error de configuración (SHA-1 o google-services). Revisa Firebase."
        7 -> "Sin conexión a Internet."
        else -> "Error de Google (${statusCode})."
    }
    is IllegalStateException -> message ?: "No se pudo obtener la cuenta de Google."
    else -> localizedMessage ?: "Ha ocurrido un error. Inténtalo de nuevo."
}
