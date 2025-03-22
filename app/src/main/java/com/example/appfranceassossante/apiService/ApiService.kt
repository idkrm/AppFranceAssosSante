package com.example.appfranceassossante.apiService
import com.example.appfranceassossante.models.User
import com.example.appfranceassossante.models.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path


interface ApiService {

    // Route pour enregistrer un utilisateur
    @POST("/register")
    fun registerUser(@Body user: User): Call<UserResponse>

    // Route pour obtenir tous les utilisateurs
    @GET("/users")
    fun getUsers(): Call<List<User>>

    // Route pour mettre à jour un utilisateur
    @PUT("/update/{id}")
    fun updateUser(@Path("id") userId: String, @Body user: User): Call<UserResponse>

    // Route pour supprimer un utilisateur
    @DELETE("/delete/{id}")
    fun deleteUser(@Path("id") userId: String): Call<UserResponse>
}

