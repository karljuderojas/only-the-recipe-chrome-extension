package com.onlytherecipe

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

abstract class JsonFileStorage<T>(private val fileName: String) {

    abstract fun fromJson(obj: JSONObject): T
    protected abstract fun toJson(item: T): JSONObject

    fun loadAll(context: Context): List<T> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return emptyList()
        return try {
            val arr = JSONArray(file.readText())
            (0 until arr.length()).map { fromJson(arr.getJSONObject(it)) }
        } catch (_: Exception) { emptyList() }
    }

    protected fun write(context: Context, items: List<T>) {
        val arr = JSONArray()
        items.forEach { arr.put(toJson(it)) }
        File(context.filesDir, fileName).writeText(arr.toString())
    }
}
