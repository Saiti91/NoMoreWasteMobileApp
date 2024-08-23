package fr.esgi.pa.nomorewasteapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.esgi.pa.nomorewasteapp.api.ProductsRequest
import fr.esgi.pa.nomorewasteapp.api.Recipe
import fr.esgi.pa.nomorewasteapp.api.RetrofitClient
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipesActivity : AppCompatActivity() {

    private val TAG = "RecipesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        val recyclerView = findViewById<RecyclerView>(R.id.recipes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Attacher un adapter vide au départ
        recyclerView.adapter = RecipesAdapter(emptyList()) { }

        // Récupérer le token depuis les SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token != null) {
            // Faire la requête à l'API /stocks/IDs
            RetrofitClient.instance.getStocks("Bearer $token").enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string()
                        if (responseBody != null) {
                            // Parse the JSON array from the response body
                            val jsonArray = JSONArray(responseBody)
                            val stocksList = mutableListOf<Int>()

                            for (i in 0 until jsonArray.length()) {
                                val stockId = jsonArray.getInt(i)
                                stocksList.add(stockId)
                            }

                            // Envoyer les IDs récupérés dans le body de la requête à /recipes/filter
                            fetchRecipes(stocksList, token, recyclerView)
                        } else {
                            Toast.makeText(this@RecipesActivity, "Aucun stock trouvé", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RecipesActivity, "Erreur lors de la récupération des stocks", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@RecipesActivity, "Échec de la requête API", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Erreur d'exécution: ${t.message}", t)
                }
            })
        } else {
            Toast.makeText(this, "Token d'authentification manquant", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRecipes(products: List<Int>, token: String, recyclerView: RecyclerView) {
        val requestBody = ProductsRequest(products)

        RetrofitClient.instance.filterRecipes("Bearer $token", requestBody).enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    val recipes = response.body()
                    if (recipes != null && recipes.isNotEmpty()) {
                        recyclerView.adapter = RecipesAdapter(recipes) { recipe ->
                            val intent = Intent(this@RecipesActivity, RecipeDetailActivity::class.java)
                            intent.putExtra("RECIPE_DATA", recipe)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this@RecipesActivity, "Aucune recette trouvée", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RecipesActivity, "Erreur lors de la récupération des recettes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                Toast.makeText(this@RecipesActivity, "Échec de la requête API", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Erreur d'exécution: ${t.message}", t)
            }
        })
    }

}
