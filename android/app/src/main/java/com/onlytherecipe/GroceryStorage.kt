package com.onlytherecipe

import android.content.Context
import org.json.JSONObject

object GroceryStorage : JsonFileStorage<GroceryItem>("grocery.json") {

    fun addItems(context: Context, items: List<String>) {
        val existing = loadAll(context).toMutableList()
        val now = System.currentTimeMillis()
        items.forEachIndexed { i, text ->
            if (text.isNotBlank()) existing.add(GroceryItem(id = now + i, text = text.trim()))
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

    override fun fromJson(obj: JSONObject) = GroceryItem(
        id      = obj.getLong("id"),
        text    = obj.getString("text"),
        checked = obj.optBoolean("checked", false)
    )

    override fun toJson(item: GroceryItem): JSONObject = JSONObject().apply {
        put("id",      item.id)
        put("text",    item.text)
        put("checked", item.checked)
    }
}
