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
    val notes: String = ""
)
