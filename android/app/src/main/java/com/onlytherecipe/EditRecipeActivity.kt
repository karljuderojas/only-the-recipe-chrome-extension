package com.onlytherecipe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditRecipeActivity : AppCompatActivity() {

    companion object {
        var pendingRecipe: Recipe? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)
        applySystemBarInsets()

        val recipe = pendingRecipe ?: run { finish(); return }
        pendingRecipe = null

        val isEditing = recipe.savedAt > 0
        renderHeader(isEditing)

        val titleEdit        = findViewById<EditText>(R.id.editTitle)
        val ingredientsEdit  = findViewById<EditText>(R.id.editIngredients)
        val instructionsEdit = findViewById<EditText>(R.id.editInstructions)

        titleEdit.setText(recipe.title)
        ingredientsEdit.setText(recipe.ingredients.joinToString("\n"))
        instructionsEdit.setText(recipe.instructions.joinToString("\n"))

        findViewById<TextView>(R.id.editCancelBtn).setOnClickListener { finish() }

        findViewById<TextView>(R.id.editSaveBtn).setOnClickListener {
            val updated = recipe.copy(
                title        = titleEdit.text.toString().trim().ifEmpty { recipe.title },
                ingredients  = ingredientsEdit.text.toString()
                    .split("\n").map { it.trim() }.filter { it.isNotEmpty() },
                instructions = instructionsEdit.text.toString()
                    .split("\n").map { it.trim() }.filter { it.isNotEmpty() }
            )
            val resultIntent = Intent()
            if (isEditing) {
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

    private fun renderHeader(isEditing: Boolean) {
        val kicker  = findViewById<TextView>(R.id.editKicker)
        val title   = findViewById<TextView>(R.id.editTitleDisplay)
        val tagline = findViewById<TextView>(R.id.editTagline)
        if (isEditing) {
            kicker.text  = "No. 03 — Edit"
            title.text   = "Edit\nrecipe."
            tagline.text = "Tweak the title, ingredients, or steps. " +
                "Source URL, timing, and notes stay as-is."
        } else {
            kicker.text  = "No. 03 — Draft"
            title.text   = "Save the\nrecipe."
            tagline.text = "Review the extracted recipe before adding it to your library."
        }
    }
}
