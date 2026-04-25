package com.onlytherecipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecipeAdapter(
    private val onClick: (Recipe) -> Unit,
    private val onDelete: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.recipeTitle)
        private val date: TextView  = view.findViewById(R.id.recipeDate)
        private val deleteBtn: Button = view.findViewById(R.id.deleteBtn)

        fun bind(recipe: Recipe) {
            title.text = recipe.title.ifEmpty { recipe.sourceUrl }
            date.text = if (recipe.savedAt > 0) {
                SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(recipe.savedAt))
            } else ""
            itemView.setOnClickListener { onClick(recipe) }
            deleteBtn.setOnClickListener { onDelete(recipe) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(a: Recipe, b: Recipe) = a.savedAt == b.savedAt
            override fun areContentsTheSame(a: Recipe, b: Recipe) = a == b
        }
    }
}
