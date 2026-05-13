package com.onlytherecipe

import android.content.Context
import androidx.core.text.HtmlCompat
import org.json.JSONArray
import org.json.JSONObject

// Some recipe sites HTML-encode their JSON-LD text (`it&#039;s` etc.). The
// extractor decodes new saves, but existing recipes were stored with entities
// baked in — decoding on load fixes them retroactively without a re-save.
private fun String.decodeEntitiesIfNeeded(): String =
    if (this.contains('&'))
        HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    else this

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
            return (0 until arr.length()).map { arr.getString(it).decodeEntitiesIfNeeded() }
        }
        fun str(key: String): String = obj.optString(key).decodeEntitiesIfNeeded()
        return Recipe(
            title        = str("title"),
            description  = str("description"),
            ingredients  = strings("ingredients"),
            instructions = strings("instructions"),
            equipment    = strings("equipment"),
            authorNotes  = str("authorNotes"),
            timingPrep   = obj.optString("timingPrep"),
            timingCook   = obj.optString("timingCook"),
            timingTotal  = obj.optString("timingTotal"),
            recipeYield  = str("recipeYield"),
            imageUrl     = obj.optString("imageUrl"),
            sourceUrl    = obj.optString("sourceUrl"),
            source       = obj.optString("source"),
            savedAt      = obj.optLong("savedAt"),
            notes        = obj.optString("notes"),
            nutrition    = nutritionFromJson(obj.optJSONObject("nutrition"))
        )
    }

    private fun nutritionFromJson(n: JSONObject?): Nutrition {
        if (n == null) return Nutrition()
        fun f(k: String) = n.optString(k).decodeEntitiesIfNeeded()
        return Nutrition(
            calories      = f("calories"),
            servingSize   = f("servingSize"),
            protein       = f("protein"),
            carbohydrates = f("carbohydrates"),
            fat           = f("fat"),
            saturatedFat  = f("saturatedFat"),
            fiber         = f("fiber"),
            sugar         = f("sugar"),
            sodium        = f("sodium"),
            cholesterol   = f("cholesterol")
        )
    }

    private fun nutritionToJson(n: Nutrition): JSONObject = JSONObject().apply {
        put("calories",      n.calories)
        put("servingSize",   n.servingSize)
        put("protein",       n.protein)
        put("carbohydrates", n.carbohydrates)
        put("fat",           n.fat)
        put("saturatedFat",  n.saturatedFat)
        put("fiber",         n.fiber)
        put("sugar",         n.sugar)
        put("sodium",        n.sodium)
        put("cholesterol",   n.cholesterol)
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
        put("nutrition",    nutritionToJson(item.nutrition))
    }
}
