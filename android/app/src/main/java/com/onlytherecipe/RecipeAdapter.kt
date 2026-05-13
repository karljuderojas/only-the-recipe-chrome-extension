package com.onlytherecipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecipeAdapter(
    private val onClick: (Recipe) -> Unit,
    private val onShare: (Recipe) -> Unit,
    private val onDelete: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val divider: View       = view.findViewById(R.id.rowDivider)
        private val index: TextView     = view.findViewById(R.id.recipeIndex)
        private val title: TextView     = view.findViewById(R.id.recipeTitle)
        private val source: TextView    = view.findViewById(R.id.recipeSource)
        private val metaDot: View       = view.findViewById(R.id.metaDot)
        private val date: TextView      = view.findViewById(R.id.recipeDate)
        private val shareBtn: ImageButton = view.findViewById(R.id.recipeShareBtn)

        fun bind(recipe: Recipe, position: Int) {
            divider.visibility = if (position == 0) View.GONE else View.VISIBLE

            index.text = "%02d".format(position + 1)
            title.text = recipe.title.ifEmpty { recipe.sourceUrl }

            // recipe.source holds the extraction signal name ("json-ld" etc.),
            // not the website domain — always derive from the source URL.
            val srcName = deriveDomain(recipe.sourceUrl)
            source.text = srcName
            source.visibility = if (srcName.isBlank()) View.GONE else View.VISIBLE

            val formattedDate = if (recipe.savedAt > 0) {
                SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(recipe.savedAt))
            } else ""
            date.text = formattedDate
            date.visibility = if (formattedDate.isBlank()) View.GONE else View.VISIBLE

            metaDot.visibility = if (source.visibility == View.VISIBLE && date.visibility == View.VISIBLE)
                View.VISIBLE else View.GONE

            itemView.setOnClickListener { onClick(recipe) }
            itemView.setOnLongClickListener {
                AlertDialog.Builder(itemView.context)
                    .setMessage("Delete \"${recipe.title.ifEmpty { "this recipe" }}\"?")
                    .setPositiveButton("Delete") { _, _ -> onDelete(recipe) }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
            shareBtn.setOnClickListener { onShare(recipe) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(a: Recipe, b: Recipe) = a.savedAt == b.savedAt
            override fun areContentsTheSame(a: Recipe, b: Recipe) = a == b
        }

        private fun deriveDomain(url: String): String {
            if (url.isBlank()) return ""
            return try {
                val host = java.net.URI(url).host ?: return ""
                if (host.startsWith("www.")) host.substring(4) else host
            } catch (_: Exception) { "" }
        }
    }
}
