package com.onlytherecipe

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object RecipeStorage {

    private const val FILE_NAME = "recipes.json"

    fun save(context: Context, recipe: Recipe) {
        val list = loadAll(context).toMutableList()
        val idx = list.indexOfFirst { it.sourceUrl == recipe.sourceUrl }
        val stamped = recipe.copy(savedAt = System.currentTimeMillis())
        if (idx >= 0) list[idx] = stamped else list.add(0, stamped)
        write(context, list)
    }

    fun loadAll(context: Context): List<Recipe> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()
        return try {
            val arr = JSONArray(file.readText())
            (0 until arr.length()).map { fromJson(arr.getJSONObject(it)) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getById(context: Context, savedAt: Long): Recipe? =
        loadAll(context).find { it.savedAt == savedAt }

    fun delete(context: Context, savedAt: Long) {
        write(context, loadAll(context).filter { it.savedAt != savedAt })
    }

    fun updateNotes(context: Context, savedAt: Long, notes: String) {
        val list = loadAll(context).toMutableList()
        val idx = list.indexOfFirst { it.savedAt == savedAt }
        if (idx >= 0) {
            list[idx] = list[idx].copy(notes = notes)
            write(context, list)
        }
    }

    fun toJsonString(recipe: Recipe): String = toJson(recipe).toString()

    fun fromJsonString(json: String): Recipe = fromJson(JSONObject(json))

    private fun write(context: Context, list: List<Recipe>) {
        val arr = JSONArray()
        list.forEach { arr.put(toJson(it)) }
        File(context.filesDir, FILE_NAME).writeText(arr.toString())
    }

    private fun toJson(r: Recipe): JSONObject = JSONObject().apply {
        put("title", r.title)
        put("description", r.description)
        put("ingredients", JSONArray(r.ingredients))
        put("instructions", JSONArray(r.instructions))
        put("equipment", JSONArray(r.equipment))
        put("authorNotes", r.authorNotes)
        put("timingPrep", r.timingPrep)
        put("timingCook", r.timingCook)
        put("timingTotal", r.timingTotal)
        put("recipeYield", r.recipeYield)
        put("imageUrl", r.imageUrl)
        put("sourceUrl", r.sourceUrl)
        put("source", r.source)
        put("savedAt", r.savedAt)
        put("notes", r.notes)
    }

    fun fromJson(obj: JSONObject): Recipe {
        fun strings(key: String): List<String> {
            val arr = obj.optJSONArray(key) ?: return emptyList()
            return (0 until arr.length()).map { arr.getString(it) }
        }
        return Recipe(
            title        = obj.optString("title"),
            description  = obj.optString("description"),
            ingredients  = strings("ingredients"),
            instructions = strings("instructions"),
            equipment    = strings("equipment"),
            authorNotes  = obj.optString("authorNotes"),
            timingPrep   = obj.optString("timingPrep"),
            timingCook   = obj.optString("timingCook"),
            timingTotal  = obj.optString("timingTotal"),
            recipeYield  = obj.optString("recipeYield"),
            imageUrl     = obj.optString("imageUrl"),
            sourceUrl    = obj.optString("sourceUrl"),
            source       = obj.optString("source"),
            savedAt      = obj.optLong("savedAt"),
            notes        = obj.optString("notes")
        )
    }
}
