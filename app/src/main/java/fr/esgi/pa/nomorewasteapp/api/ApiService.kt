package fr.esgi.pa.nomorewasteapp.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

// Data classes to handle the login request and response
data class LoginRequest(val email: String, val password: String)

data class LoginResponse(val message: String, val token: String?, val user: User?)

data class User(val id: Int, val name: String, val email: String)

data class ProductsRequest(val products: List<Int>)

interface ApiService {
    @POST("/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("/stocks/IDs")
    fun getStocks(@Header("Authorization") authHeader: String): Call<ResponseBody>

    @POST("/recipes/filter")
    fun filterRecipes(
        @Header("Authorization") authHeader: String,
        @Body requestBody: ProductsRequest
    ): Call<List<Recipe>>

}
