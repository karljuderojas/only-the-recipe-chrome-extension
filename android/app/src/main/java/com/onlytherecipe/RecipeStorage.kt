package com.onlytherecipe

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object RecipeStorage : JsonFileStorage<Recipe>("recipes.json") {

    fun save(context: Context, recipe: Recipe) {
        val list = loadAll(context).toMutableList()
        val idx = list.indexOfFirst { it.sourceUrl == recipe.sourceUrl }
        val stamped = recipe.copy(savedAt = System.currentTimeMillis())
        if (idx >= 0) list[idx] = stamped else list.add(0, stamped)
        write(context, list)
    }

    fun getById(context: Context, savedAt: Long): Recipe? =
        loadAll(context).find { it.savedAt == savedAt }

    fun delete(context: Context, savedAt: Long) {
        write(context, loadAll(context).filter { it.savedAt != savedAt })
    }

    fun update(context: Context, recipe: Recipe) {
        val list = loadAll(context).toMutableList()
        val idx = list.indexOfFirst { it.savedAt == recipe.savedAt }
        if (idx >= 0) {
            list[idx] = recipe
            write(context, list)
        }
    }

    fun updateNotes(context: Context, savedAt: Long, notes: String) {
        val list = loadAll(context).toMutableList()
        val idx = list.indexOfFirst { it.savedAt == savedAt }
        if (idx >= 0) {
            list[idx] = list[idx].copy(notes = notes)
            write(context, list)
        }
    }

    override fun fromJson(obj: JSONObject): Recipe {
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

    override fun toJson(item: Recipe): JSONObject = JSONObject().apply {
        put("title",        item.title)
        put("description",  item.description)
        put("ingredients",  JSONArray(item.ingredients))
        put("instructions", JSONArray(item.instructions))
        put("equipment",    JSONArray(item.equipment))
        put("authorNotes",  item.authorNotes)
        put("timingPrep",   item.timingPrep)
        put("timingCook",   item.timingCook)
        put("timingTotal",  item.timingTotal)
        put("recipeYield",  item.recipeYield)
        put("imageUrl",     item.imageUrl)
        put("sourceUrl",    item.sourceUrl)
        put("source",       item.source)
        put("savedAt",      item.savedAt)
        put("notes",        item.notes)
    }
}
