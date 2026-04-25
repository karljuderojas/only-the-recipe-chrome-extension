package com.onlytherecipe

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object GroceryStorage {

    private const val FILE = "grocery.json"

    fun loadAll(context: Context): List<GroceryItem> {
        val file = context.filesDir.resolve(FILE)
        if (!file.exists()) return emptyList()
        return try {
            val arr = JSONArray(file.readText())
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                GroceryItem(
                    id      = o.getLong("id"),
                    text    = o.getString("text"),
                    checked = o.optBoolean("checked", false)
                )
            }
        } catch (_: Exception) { emptyList() }
    }

    fun addItems(context: Context, items: List<String>) {
        val existing = loadAll(context).toMutableList()
        val now = System.currentTimeMillis()
        items.forEachIndexed { i, text ->
            if (text.isNotBlank()) existing.add(GroceryItem(id = now + i, text = text.trim()))
        }
        save(context, existing)
    }

    fun toggleChecked(context: Context, id: Long) {
        save(context, loadAll(context).map { if (it.id == id) it.copy(checked = !it.checked) else it })
    }

    fun deleteItem(context: Context, id: Long) {
        save(context, loadAll(context).filter { it.id != id })
    }

    fun clearChecked(context: Context) {
        save(context, loadAll(context).filter { !it.checked })
    }

    private fun save(context: Context, items: List<GroceryItem>) {
        val arr = JSONArray()
        items.forEach { item ->
            arr.put(JSONObject().apply {
                put("id",      item.id)
                put("text",    item.text)
                put("checked", item.checked)
            })
        }
        context.filesDir.resolve(FILE).writeText(arr.toString())
    }
}
