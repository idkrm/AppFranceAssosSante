package com.example.appfranceassossante.models

// Représente la réponse de l'API lorsqu'un utilisateur est créé, mis à jour, etc.
data class UserResponse(
    val message: String, // Le message de succès ou d'erreur
    val user: User? = null // L'utilisateur créé ou modifié, peut être null dans certains cas
)
