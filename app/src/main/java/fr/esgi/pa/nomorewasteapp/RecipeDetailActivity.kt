package fr.esgi.pa.nomorewasteapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import fr.esgi.pa.nomorewasteapp.api.Recipe
import fr.esgi.pa.nomorewasteapp.api.RetrofitClient

class RecipeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)  // Assurez-vous que le bon layout est utilisé

        val recipeImage: ImageView = findViewById(R.id.recipe_image)  // Vérifiez que cet ID existe
        val recipeName: TextView = findViewById(R.id.recipe_name)
        val recipeInstructions: TextView = findViewById(R.id.recipe_instructions)
        val recipeIngredients: TextView = findViewById(R.id.recipe_ingredients)

        // Récupérer l'objet Recipe passé via l'Intent
        val recipe = intent.getParcelableExtra<Recipe>("RECIPE_DATA")

        // Vérifiez que la recette est non nulle avant de l'afficher
        if (recipe != null) {
            recipeName.text = recipe.Name
            recipeInstructions.text = recipe.Instructions

            // Afficher les ingrédients sous forme de liste
            val ingredientsText = recipe.Ingredients.joinToString(separator = "\n") {
                val quantityText = if (it.Quantity != null) {
                    if (it.Quantity.endsWith(".00")) {
                        it.Quantity.substringBefore(".00")
                    } else {
                        it.Quantity
                    }
                } else {
                    ""
                }

                if (quantityText.isEmpty() && it.Unit.isNullOrEmpty()) {
                    "${it.Name} : au goût"
                } else {
                    "${it.Name} : $quantityText ${it.Unit ?: ""}"
                }
            }
            recipeIngredients.text = ingredientsText

            // Construire l'URL de l'image en utilisant l'ID de la recette
            val imageUrl = "${RetrofitClient.IMAGE_BASE_URL}${recipe.Recipes_ID}.jpg"

            // Charger l'image avec Glide
            Glide.with(this).load(imageUrl).into(recipeImage)
        }
    }
}
