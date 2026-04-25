package com.onlytherecipe

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditRecipeActivity : AppCompatActivity() {

    companion object {
        var pendingRecipe: Recipe? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        val recipe = pendingRecipe ?: run { finish(); return }
        pendingRecipe = null

        val titleEdit        = findViewById<EditText>(R.id.editTitle)
        val ingredientsEdit  = findViewById<EditText>(R.id.editIngredients)
        val instructionsEdit = findViewById<EditText>(R.id.editInstructions)

        titleEdit.setText(recipe.title)
        ingredientsEdit.setText(recipe.ingredients.joinToString("\n"))
        instructionsEdit.setText(recipe.instructions.joinToString("\n"))

        findViewById<Button>(R.id.editCancelBtn).setOnClickListener { finish() }

        findViewById<Button>(R.id.editSaveBtn).setOnClickListener {
            val updated = recipe.copy(
                title        = titleEdit.text.toString().trim().ifEmpty { recipe.title },
                ingredients  = ingredientsEdit.text.toString()
                    .split("\n").map { it.trim() }.filter { it.isNotEmpty() },
                instructions = instructionsEdit.text.toString()
                    .split("\n").map { it.trim() }.filter { it.isNotEmpty() }
            )
            val resultIntent = Intent()
            if (recipe.savedAt > 0) {
                RecipeStorage.update(this, updated)
                resultIntent.putExtra("saved_at", recipe.savedAt)
            } else {
                RecipeStorage.save(this, updated)
            }
            Toast.makeText(this, "Recipe saved.", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
