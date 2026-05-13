package com.onlytherecipe

import android.content.Context
import org.json.JSONObject

object GroceryStorage : JsonFileStorage<GroceryItem>("grocery.json") {

    // Items added without a recipe context (manual add or pre-migration items)
    // group under "Other" in the UI. Pass recipeSavedAt=null + recipeName="".
    fun addItems(
        context: Context,
        items: List<String>,
        recipeSavedAt: Long? = null,
        recipeName: String = ""
    ) {
        val existing = loadAll(context).toMutableList()
        val now = System.currentTimeMillis()
        items.forEachIndexed { i, text ->
            if (text.isNotBlank()) {
                existing.add(GroceryItem(
                    id = now + i,
                    text = text.trim(),
                    recipeSavedAt = recipeSavedAt,
                    recipeName = recipeName
                ))
            }
        }
        write(context, existing)
    }

    fun toggleChecked(context: Context, id: Long) {
        write(context, loadAll(context).map { if (it.id == id) it.copy(checked = !it.checked) else it })
    }

    fun deleteItem(context: Context, id: Long) {
        write(context, loadAll(context).filter { it.id != id })
    }

    fun clearChecked(context: Context) {
        write(context, loadAll(context).filter { !it.checked })
    }

    fun clearAll(context: Context) {
        write(context, emptyList())
    }

    override fun fromJson(obj: JSONObject) = GroceryItem(
        id            = obj.getLong("id"),
        text          = obj.getString("text"),
        checked       = obj.optBoolean("checked", false),
        recipeSavedAt = if (obj.has("recipeSavedAt") && !obj.isNull("recipeSavedAt"))
            obj.getLong("recipeSavedAt") else null,
        recipeName    = obj.optString("recipeName", "")
    )

    override fun toJson(item: GroceryItem): JSONObject = JSONObject().apply {
        put("id",      item.id)
        put("text",    item.text)
        put("checked", item.checked)
        if (item.recipeSavedAt != null) put("recipeSavedAt", item.recipeSavedAt)
        if (item.recipeName.isNotBlank()) put("recipeName", item.recipeName)
    }
}
