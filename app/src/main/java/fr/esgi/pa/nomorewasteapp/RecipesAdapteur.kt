package fr.esgi.pa.nomorewasteapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.esgi.pa.nomorewasteapp.api.Recipe
import fr.esgi.pa.nomorewasteapp.api.RetrofitClient

// RecipeAdapter.kt
class RecipesAdapter(private val recipes: List<Recipe>, private val onClick: (Recipe) -> Unit) :
    RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(view: View, val onClick: (Recipe) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val recipeName: TextView = view.findViewById(R.id.recipe_name)
        private val recipeImage: ImageView = view.findViewById(R.id.recipe_image)
        private var currentRecipe: Recipe? = null

        init {
            view.setOnClickListener {
                currentRecipe?.let {
                    onClick(it)
                }
            }
        }

        fun bind(recipe: Recipe) {
            currentRecipe = recipe
            recipeName.text = recipe.Name

            // Construire l'URL de l'image en utilisant RetrofitClient.IMAGE_BASE_URL
            val imageUrl = "${RetrofitClient.IMAGE_BASE_URL}${recipe.Recipes_ID}.jpg"
            // Charger l'image avec Glide
            Glide.with(itemView.context).load(imageUrl).into(recipeImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount() = recipes.size
}
