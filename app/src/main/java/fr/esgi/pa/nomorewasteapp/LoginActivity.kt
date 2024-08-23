package fr.esgi.pa.nomorewasteapp

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.esgi.pa.nomorewasteapp.api.LoginRequest
import fr.esgi.pa.nomorewasteapp.api.LoginResponse
import fr.esgi.pa.nomorewasteapp.api.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val emailEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)

        emailEditText.setText("admin@user.com")
        passwordEditText.setText("password")

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer votre email et mot de passe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(email, password)

            RetrofitClient.instance.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val token = response.headers()["authorization"]?.substringAfter("Bearer ")

                        Toast.makeText(this@LoginActivity, loginResponse?.message ?: "Connexion réussie", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Réponse réussie: $loginResponse")

                        if (token != null) {
                            Log.d(TAG, "Token: $token")

                            // Décoder le token pour récupérer l'ID de l'utilisateur
                            val userId = decodeJwtAndGetUserId(token)
                            if (userId != null) {
                                // Stocker le token et l'ID de l'utilisateur dans SharedPreferences
                                val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("auth_token", token)
                                editor.putString("user_id", userId)
                                editor.apply()

                                Log.d(TAG, "User ID: $userId enregistré avec succès")
                                val intent = Intent(this@LoginActivity, RecipesActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.e(TAG, "Impossible de récupérer l'ID de l'utilisateur à partir du token")
                            }
                        } else {
                            Log.e(TAG, "Le token est absent de la réponse")
                        }

                        // Gérer la réponse avec succès, par exemple, rediriger vers une autre activité
                    } else {
                        Toast.makeText(this@LoginActivity, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Erreur de connexion: Code ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Erreur: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Erreur d'exécution: ${t.message}", t)
                }
            })
        }
    }

    // Fonction pour décoder le JWT et extraire l'ID de l'utilisateur
    private fun decodeJwtAndGetUserId(token: String): String? {
        try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = parts[1]
                val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
                val decodedString = String(decodedBytes, Charsets.UTF_8)
                Log.d(TAG, "Decoded JWT payload: $decodedString")
                val jsonObject = JSONObject(decodedString)
                return jsonObject.getString("uid")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors du décodage du JWT: ${e.message}", e)
        }
        return null
    }
}
