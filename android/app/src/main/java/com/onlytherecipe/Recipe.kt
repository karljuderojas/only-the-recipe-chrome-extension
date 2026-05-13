package com.onlytherecipe

data class Recipe(
    val title: String,
    val description: String,
    val ingredients: List<String>,
    // Plain instruction steps; section headers are prefixed with "§:" (e.g., "§:Making the Sauce")
    val instructions: List<String>,
    val equipment: List<String>,
    val authorNotes: String,
    val timingPrep: String,
    val timingCook: String,
    val timingTotal: String,
    val recipeYield: String,
    val imageUrl: String,
    val sourceUrl: String,
    val source: String,
    val savedAt: Long = 0L,
    val notes: String = "",
    val nutrition: Nutrition = Nutrition()
)

// Mirrors Schema.org NutritionInformation. All fields are raw strings as
// they appear in JSON-LD ("320 calories", "15 g", "450 mg", etc.) — the UI
// is responsible for any reformatting. Empty string = missing.
data class Nutrition(
    val calories: String = "",
    val servingSize: String = "",
    val protein: String = "",
    val carbohydrates: String = "",
    val fat: String = "",
    val saturatedFat: String = "",
    val fiber: String = "",
    val sugar: String = "",
    val sodium: String = "",
    val cholesterol: String = ""
) {
    fun isEmpty(): Boolean = calories.isBlank() && servingSize.isBlank() &&
        protein.isBlank() && carbohydrates.isBlank() && fat.isBlank() &&
        saturatedFat.isBlank() && fiber.isBlank() && sugar.isBlank() &&
        sodium.isBlank() && cholesterol.isBlank()
}
